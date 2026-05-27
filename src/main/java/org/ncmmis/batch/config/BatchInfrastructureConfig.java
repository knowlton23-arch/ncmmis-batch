package org.ncmmis.batch.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
@Import(DataSourceConfig.class)
@ComponentScan("org.ncmmis.batch.common")
@PropertySource("classpath:application.properties")
public class BatchInfrastructureConfig {

	@Bean
    JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

	@Bean
	JobOperatorFactoryBean jobOperator(JobRepository jobRepository) {
		JobOperatorFactoryBean jobOperatorFactoryBean = new JobOperatorFactoryBean();
		jobOperatorFactoryBean.setJobRepository(jobRepository);
		return jobOperatorFactoryBean;
	}
	
//  @Bean
//  public JobParametersConverter jobParametersConverter() {
//      // Implement this interface for adding CommandLineJobOperator params dynamically
//      return new MyCustomJobParametersConverter(); 
//  }
	
//	@Bean
//	public ExitCodeMapper exitCodeMapper() {
//		// Implement this interface for adding CommandLineJobOperator exit codes > 2
//		return new MyCustomExitCodeMapper();
//	}
	
}
