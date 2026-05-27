package org.ncmmis.batch.provider.job.job2;

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
import org.springframework.batch.infrastructure.item.ItemProcessor;
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

@Configuration
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderJob2 {
	
	@Bean
	Job job2(
			JobRepository jobRepository, 
			Step providerLoad,
			CustomJobExecutionListener customJobExecutionListener) {
		
		return new JobBuilder(jobRepository)
				.start(providerLoad)
				.incrementer(new RunIdIncrementer())
				.listener(customJobExecutionListener)
				.build();
	}
	
	@Bean
	Step providerLoad(
			JobRepository jobRepository, 
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerFileItemReader,
	        ItemProcessor<Provider, Provider> providerLoadItemProcessor,
	        ItemWriter<Provider> providerItemWriter,
	        CustomStepExecutionListener customStepExecutionListener,
	        CustomChunkListener<Provider, Provider> customChunkListener) {	
		
		return new StepBuilder("providerLoad", jobRepository).<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerFileItemReader)
			.processor(providerLoadItemProcessor)
			.writer(providerItemWriter)
			.listener(customStepExecutionListener)
			.listener(customChunkListener)
			.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerFileItemReader() {		
		return new FlatFileItemReaderBuilder<Provider>().name("providerFileItemReader")
			.resource(new ClassPathResource("data/input/provider/job2/providers.csv"))
			.linesToSkip(1)
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}
	
	@Bean
	ItemProcessor<Provider, Provider> providerLoadItemProcessor() {
		return new ProviderLoadItemProcessor();
	}
	
	@Bean
	JdbcBatchItemWriter<Provider> providerItemWriter(
			DataSource dataSource, 
			@Value("${provider.insert}") String sql) {
		
	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
