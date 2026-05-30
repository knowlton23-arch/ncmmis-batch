package org.ncmmis.batch.provider.job.skip;

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

@Configuration("providerSkipDemoJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderSkipDemoJob {

	@Bean
	Job providerSkipDemoJob(
			JobRepository jobRepository,
			Step providerSkipDemoStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerSkipDemoStep)
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerSkipDemoStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerSkipDemoItemReader,
	        ProviderSkipDemoItemProcessor providerSkipDemoItemProcessor,
	        ItemWriter<Provider> providerSkipDemoItemWriter,
	        ProviderSkipDemoSkipListener providerSkipDemoSkipListener,
	        CustomStepExecutionListener stepExecutionListener,
	        CustomChunkListener<Provider, Provider> chunkListener) {

		return new StepBuilder("providerSkipDemoStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerSkipDemoItemReader)
			.processor(providerSkipDemoItemProcessor)
			.writer(providerSkipDemoItemWriter)
			.faultTolerant()
			.skip(ProviderSkipDemoException.class)
			.skipLimit(10)
			.listener(providerSkipDemoSkipListener)
			.listener(stepExecutionListener)
			.listener(chunkListener)
			.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerSkipDemoItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerSkipDemoItemReader")
			.resource(new ClassPathResource("data/input/provider/skip/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderSkipDemoItemProcessor providerSkipDemoItemProcessor() {
		return new ProviderSkipDemoItemProcessor();
	}

	@Bean
	ProviderSkipDemoSkipListener providerSkipDemoSkipListener() {
		return new ProviderSkipDemoSkipListener();
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerSkipDemoItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
