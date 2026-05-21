package org.ncmmis.batch.provider.job;

import javax.sql.DataSource;

import org.ncmmis.batch.common.CustomChunkListener;
import org.ncmmis.batch.common.CustomJobExecutionListener;
import org.ncmmis.batch.common.CustomStepExecutionListener;
import org.ncmmis.batch.config.JobConfig;
import org.ncmmis.batch.provider.entity.Provider;
import org.ncmmis.batch.provider.entity.ProviderDao;
import org.ncmmis.batch.provider.entity.ProviderFieldSetMapper;
import org.ncmmis.batch.provider.entity.ProviderItemWriter;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class ProviderJob2 extends JobConfig {
	
	@Autowired
	private CustomJobExecutionListener customJobExecutionListener;
	
	@Autowired
	private CustomStepExecutionListener customStepExecutionListener;
	
	@Autowired
	private CustomChunkListener<Provider, Provider> customChunkListener;
	
	@Bean
	Job job2(JobRepository jobRepository, Step providerLoad) {
		return new JobBuilder(jobRepository)
				.start(providerLoad)
				.incrementer(new RunIdIncrementer())
				.listener(customJobExecutionListener)
				.build();
	}
	
	@Bean
	Step providerLoad(JobRepository jobRepository, JdbcTransactionManager transactionManager,
		FlatFileItemReader<Provider> providerFileItemReader, ProviderItemWriter providerItemWriter) {	
		return new StepBuilder("providerLoad", jobRepository).<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerFileItemReader)
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
	ProviderItemWriter providerItemWriter(DataSource dataSource) {
		ProviderItemWriter providerItemWriter = new ProviderItemWriter();
		ProviderDao providerDao = new ProviderDao();
		providerDao.setDataSource(dataSource);
		providerItemWriter.setProviderDao(providerDao);
		return providerItemWriter;
	}
	
}
