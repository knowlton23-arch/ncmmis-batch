package org.ncmmis.batch.recipient.job.hello;

import org.ncmmis.batch.config.BatchInfrastructureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Configuration("helloWorldJobConfig")
@Import(BatchInfrastructureConfig.class)
public class HelloWorldJob {

	private static final Logger log = LoggerFactory.getLogger(HelloWorldJob.class);
	
	@Bean
	Job helloWorldJob(JobRepository jobRepository, Step helloWorldStep) {
		return new JobBuilder(jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(helloWorldStep)
				.build();
	}
	
	@Bean
	Step helloWorldStep(JobRepository jobRepository) {
		return new StepBuilder(jobRepository).tasklet((contribution, chunkContext) -> {
			String name = (String) chunkContext.getStepContext().getJobParameters().get("name");
			if(name == null || name.isEmpty()) {
				name = new String("World");
			}
			log.info("Hello, {}!", name);
			return RepeatStatus.FINISHED;	
		}).build();
	}
}
