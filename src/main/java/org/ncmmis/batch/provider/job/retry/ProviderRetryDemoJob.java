package org.ncmmis.batch.provider.job.retry;

import javax.sql.DataSource;

import org.ncmmis.batch.common.CustomJobExecutionListener;
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

@Configuration("providerRetryDemoJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderRetryDemoJob {

	@Bean
	Job providerRetryDemoJob(
			JobRepository jobRepository,
			Step providerRetryDemoStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerRetryDemoStep)
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerRetryDemoStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerRetryDemoItemReader,
	        ProviderRetryDemoItemProcessor providerRetryDemoItemProcessor,
	        ItemWriter<Provider> providerRetryDemoItemWriter,
	        ProviderRetryDemoRetryListener providerRetryDemoRetryListener) {

		return new StepBuilder("providerRetryDemoStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerRetryDemoItemReader)
			.processor(providerRetryDemoItemProcessor)
			.writer(providerRetryDemoItemWriter)
			.faultTolerant()
			.retry(ProviderRetryDemoException.class)
			.retryLimit(3)
			.retryListener(providerRetryDemoRetryListener)
			.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerRetryDemoItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerRetryDemoItemReader")
			.resource(new ClassPathResource("data/input/provider/retry/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderRetryDemoItemProcessor providerRetryDemoItemProcessor() {
		return new ProviderRetryDemoItemProcessor();
	}

	@Bean
	ProviderRetryDemoRetryListener providerRetryDemoRetryListener() {
		return new ProviderRetryDemoRetryListener();
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerRetryDemoItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
