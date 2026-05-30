package org.ncmmis.batch.provider.job.validation;

import javax.sql.DataSource;

import org.ncmmis.batch.common.CustomChunkListener;
import org.ncmmis.batch.common.CustomJobExecutionListener;
import org.ncmmis.batch.common.CustomStepExecutionListener;
import org.ncmmis.batch.config.BatchInfrastructureConfig;
import org.ncmmis.batch.provider.entity.Provider;
import org.ncmmis.batch.provider.entity.ProviderFieldSetMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration("providerValidationJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderValidationJob {

	@Bean
	Job providerValidationJob(
			JobRepository jobRepository,
			Step providerValidationStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerValidationStep)
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerValidationStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerValidationItemReader,
	        ProviderValidationItemProcessor providerValidationItemProcessor,
	        ItemWriter<Provider> providerValidationItemWriter,
	        CustomStepExecutionListener stepExecutionListener,
	        CustomChunkListener<Provider, Provider> chunkListener) {

		return new StepBuilder("providerValidationStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerValidationItemReader)
			.processor(providerValidationItemProcessor)
			.writer(providerValidationItemWriter)
			.listener(stepExecutionListener)
			.listener(chunkListener)
			.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerValidationItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerValidationItemReader")
			.resource(new ClassPathResource("data/input/provider/job4/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderValidationItemProcessor providerValidationItemProcessor() {
		return new ProviderValidationItemProcessor();
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerValidationItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
