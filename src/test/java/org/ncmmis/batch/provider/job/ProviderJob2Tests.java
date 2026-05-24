package org.ncmmis.batch.provider.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public class ProviderJob2Tests {
	
	@Test
	public void testLaunchJob2() throws Exception {
		// given
		ApplicationContext context = new AnnotationConfigApplicationContext(ProviderJob2.class);
		JobOperator jobOperator = context.getBean(JobOperator.class);
		Job job = context.getBean(Job.class);

		// when
		JobParameters jobParameters = new JobParameters();		
		JobExecution jobExecution = jobOperator.start(job, jobParameters);

		// then
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		assertEquals(1, jobExecution.getStepExecutions().size());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStepExecutions().iterator().next().getStatus());
		
		int count = JdbcTestUtils.countRowsInTable(new JdbcTemplate(context.getBean(DataSource.class)), "mmis_provider");
		assertTrue(count > 0);
	}
}
