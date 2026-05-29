package org.ncmmis.batch.provider.job.restart;

import javax.sql.DataSource;

import org.ncmmis.batch.common.CustomChunkListener;
import org.ncmmis.batch.common.CustomJobExecutionListener;
import org.ncmmis.batch.common.CustomStepExecutionListener;
import org.ncmmis.batch.config.BatchInfrastructureConfig;
import org.ncmmis.batch.provider.entity.Provider;
import org.ncmmis.batch.provider.entity.ProviderFieldSetMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration("providerRestartDemoJobConfig")
@Import(BatchInfrastructureConfig.class)
@PropertySource("classpath:sql/provider-sql.properties")
public class ProviderRestartDemoJob {

	@Bean
	Job providerRestartDemoJob(
			JobRepository jobRepository,
			Step providerRestartDemoStep,
			CustomJobExecutionListener jobExecutionListener) {

		return new JobBuilder(jobRepository)
				.start(providerRestartDemoStep)
				.listener(jobExecutionListener)
				.build();
	}

	@Bean
	Step providerRestartDemoStep(
			JobRepository jobRepository,
			JdbcTransactionManager transactionManager,
	        ItemReader<Provider> providerRestartDemoItemReader,
	        ProviderRestartDemoItemProcessor providerRestartDemoItemProcessor,
	        ItemWriter<Provider> providerRestartDemoItemWriter,
	        CustomStepExecutionListener stepExecutionListener,
	        CustomChunkListener<Provider, Provider> chunkListener) {

		return new StepBuilder("providerRestartDemoStep", jobRepository)
			.<Provider, Provider>chunk(100)
			.transactionManager(transactionManager)
			.reader(providerRestartDemoItemReader)
			.processor(providerRestartDemoItemProcessor)
			.writer(providerRestartDemoItemWriter)
			.listener(providerRestartDemoItemProcessor)
			.listener(stepExecutionListener)
			.listener(chunkListener)
			.build();
	}

	@Bean
	FlatFileItemReader<Provider> providerRestartDemoItemReader() {
		return new FlatFileItemReaderBuilder<Provider>()
			.name("providerRestartDemoItemReader")
			.resource(new ClassPathResource("data/input/provider/job3/providers.csv"))
			.delimited()
			.names("id", "npi", "lastName", "firstName", "ssn", "email")
			.linesToSkip(1)
			.fieldSetMapper(new ProviderFieldSetMapper())
			.build();
	}

	@Bean
	ProviderRestartDemoItemProcessor providerRestartDemoItemProcessor(JobRepository jobRepository) {
		return new ProviderRestartDemoItemProcessor(jobRepository);
	}

	@Bean
	JdbcBatchItemWriter<Provider> providerRestartDemoItemWriter(
			DataSource dataSource,
			@Value("${provider.insert}") String sql) {

	    return new JdbcBatchItemWriterBuilder<Provider>()
	        .dataSource(dataSource)
	        .sql(sql)
	        .beanMapped()
	        .build();
	}
}
