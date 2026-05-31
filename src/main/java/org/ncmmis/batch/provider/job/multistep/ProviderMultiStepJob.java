package org.ncmmis.batch.provider.job.multistep;

import javax.sql.DataSource;

import org.ncmmis.batch.common.CustomJobExecutionListener;
import org.ncmmis.batch.config.BatchInfrastructureConfig;
import org.ncmmis.batch.provider.entity.Provider;
import org.ncmmis.batch.provider.entity.ProviderFieldSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
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

@Configuration("providerMultiStepJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderMultiStepJob {

	private static final Logger log = LoggerFactory.getLogger(ProviderMultiStepJob.class);

	@Bean
	Job providerMultiStepJob(
			JobRepository jobRepository,
			Step providerMultiStepCleanupStep,
			Step providerMultiStepLoadStep,
			Step providerMultiStepSummaryStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerMultiStepCleanupStep)
				.next(providerMultiStepLoadStep)
				.next(providerMultiStepSummaryStep)
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerMultiStepCleanupStep(
			JobRepository jobRepository,
			DataSource dataSource) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		return new StepBuilder("providerMultiStepCleanupStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					int deletedCount = jdbcTemplate.update("delete from ncmmis_provider");
					log.info("ProviderMultiStepJob cleanup deleted {} provider row(s).", deletedCount);
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	Step providerMultiStepLoadStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerMultiStepItemReader,
	        ProviderMultiStepItemProcessor providerMultiStepItemProcessor,
	        ItemWriter<Provider> providerMultiStepItemWriter) {

		return new StepBuilder("providerMultiStepLoadStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerMultiStepItemReader)
			.processor(providerMultiStepItemProcessor)
			.writer(providerMultiStepItemWriter)
			.build();
	}

	@Bean
	Step providerMultiStepSummaryStep(
			JobRepository jobRepository,
			DataSource dataSource) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		return new StepBuilder("providerMultiStepSummaryStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					Integer providerCount = jdbcTemplate.queryForObject(
							"select count(*) from ncmmis_provider",
							Integer.class);
					log.info("ProviderMultiStepJob summary found {} provider row(s).", providerCount);
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerMultiStepItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerMultiStepItemReader")
			.resource(new ClassPathResource("data/input/provider/multistep/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderMultiStepItemProcessor providerMultiStepItemProcessor() {
		return new ProviderMultiStepItemProcessor();
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerMultiStepItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
