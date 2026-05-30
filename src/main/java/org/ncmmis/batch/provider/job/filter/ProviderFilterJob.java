package org.ncmmis.batch.provider.job.filter;

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

@Configuration("providerFilterJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderFilterJob {

	@Bean
	Job providerFilterJob(
			JobRepository jobRepository,
			Step providerFilterStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerFilterStep)
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerFilterStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerFilterItemReader,
	        ProviderFilterItemProcessor providerFilterItemProcessor,
	        ItemWriter<Provider> providerFilterItemWriter) {

		return new StepBuilder("providerFilterStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerFilterItemReader)
			.processor(providerFilterItemProcessor)
			.writer(providerFilterItemWriter)
			.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerFilterItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerFilterItemReader")
			.resource(new ClassPathResource("data/input/provider/filter/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderFilterItemProcessor providerFilterItemProcessor() {
		return new ProviderFilterItemProcessor();
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerFilterItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
