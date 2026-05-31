package org.ncmmis.batch.provider.job.conditional;

import javax.sql.DataSource;

import org.ncmmis.batch.common.CustomJobExecutionListener;
import org.ncmmis.batch.config.BatchInfrastructureConfig;
import org.ncmmis.batch.provider.entity.Provider;
import org.ncmmis.batch.provider.entity.ProviderFieldSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration("providerConditionalFlowJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderConditionalFlowJob {

	static final String EMPTY_DATA = "EMPTY_DATA";
	static final String VALID_DATA = "VALID_DATA";

	private static final Logger log = LoggerFactory.getLogger(ProviderConditionalFlowJob.class);

	@Bean
	Job providerConditionalFlowJob(
			JobRepository jobRepository,
			Step providerConditionalValidationStep,
			Step providerConditionalLoadStep,
			Step providerConditionalLoadedSummaryStep,
			Step providerConditionalEmptySummaryStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerConditionalValidationStep)
					.on(VALID_DATA).to(providerConditionalLoadStep)
						.next(providerConditionalLoadedSummaryStep)
				.from(providerConditionalValidationStep)
					.on(EMPTY_DATA).to(providerConditionalEmptySummaryStep)
				.end()
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerConditionalValidationStep(JobRepository jobRepository) {
		return new StepBuilder("providerConditionalValidationStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					String inputMode = (String) chunkContext.getStepContext()
							.getJobParameters()
							.getOrDefault("inputMode", "valid");
					String exitCode = "empty".equalsIgnoreCase(inputMode) ? EMPTY_DATA : VALID_DATA;

					contribution.setExitStatus(new ExitStatus(exitCode));
					log.info("ProviderConditionalFlowJob validation selected {} branch.", exitCode);

					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	Step providerConditionalLoadStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerConditionalItemReader,
	        ProviderConditionalFlowItemProcessor providerConditionalFlowItemProcessor,
	        ItemWriter<Provider> providerConditionalItemWriter) {

		return new StepBuilder("providerConditionalLoadStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerConditionalItemReader)
			.processor(providerConditionalFlowItemProcessor)
			.writer(providerConditionalItemWriter)
			.build();
	}

	@Bean
	Step providerConditionalLoadedSummaryStep(
			JobRepository jobRepository,
			DataSource dataSource) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		return new StepBuilder("providerConditionalLoadedSummaryStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					Integer providerCount = jdbcTemplate.queryForObject(
							"select count(*) from ncmmis_provider",
							Integer.class);
					log.info("ProviderConditionalFlowJob loaded branch found {} provider row(s).", providerCount);
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	Step providerConditionalEmptySummaryStep(JobRepository jobRepository) {
		return new StepBuilder("providerConditionalEmptySummaryStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					log.info("ProviderConditionalFlowJob empty branch skipped provider loading.");
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerConditionalItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerConditionalItemReader")
			.resource(new ClassPathResource("data/input/provider/conditional/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderConditionalFlowItemProcessor providerConditionalFlowItemProcessor() {
		return new ProviderConditionalFlowItemProcessor();
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerConditionalItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
