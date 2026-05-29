package org.ncmmis.batch.provider.job.hello;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBatchTest
@SpringJUnitConfig(HelloWorldJob.class)
public class HelloWorldJobTests {
	
    private final JobOperator jobOperator;
    private final Job job;

    @Autowired
    HelloWorldJobTests(JobOperator jobOperator, Job job, DataSource dataSource) {
        this.jobOperator = jobOperator;
        this.job = job;
    }

	@Test
	public void testLaunchHelloWorldJob() throws Exception {
		
        JobExecution jobExecution = jobOperator.start(job, new JobParameters());

		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		assertEquals(1, jobExecution.getStepExecutions().size());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStepExecutions().iterator().next().getStatus());
	}
}
