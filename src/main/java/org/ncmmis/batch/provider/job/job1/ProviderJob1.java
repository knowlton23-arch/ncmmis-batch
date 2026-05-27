package org.ncmmis.batch.provider.job.job1;

import org.ncmmis.batch.config.BatchInfrastructureConfig;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BatchInfrastructureConfig.class)
public class ProviderJob1 {
	
	@Bean
	Job job1(JobRepository jobRepository, Step job1Step1) {
		return new JobBuilder(jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(job1Step1)
				.build();
	}
	
	@Bean
	Step job1Step1(JobRepository jobRepository) {
		return new StepBuilder(jobRepository).tasklet((contribution, chunkContext) -> {
			String name = (String) chunkContext.getStepContext().getJobParameters().get("name");
			if(name == null || name.isEmpty()) {
				name = new String("World");
			}
			System.out.println(String.format("Hello, %s!", name));
			return RepeatStatus.FINISHED;	
		}).build();
	}
}
