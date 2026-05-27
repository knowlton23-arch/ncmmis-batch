package org.ncmmis.batch.provider.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ncmmis.batch.provider.job.job2.ProviderJob2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBatchTest
@SpringJUnitConfig(ProviderJob2.class)
public class ProviderJob2Tests {
	
	@Test
	public void testLaunchJob2() throws Exception {
				
		// given
		@SuppressWarnings("resource")
		ApplicationContext context = new AnnotationConfigApplicationContext(ProviderJob2.class);
		
		// Delete all existing rows from the table
		JdbcTestUtils.deleteFromTables(new JdbcTemplate(context.getBean(DataSource.class)), "ncmmis_provider");

		JobOperator jobOperator = context.getBean(JobOperator.class);
		Job job = context.getBean(Job.class);

		// when
		JobParameters jobParameters = new JobParameters();		
		JobExecution jobExecution = jobOperator.start(job, jobParameters);

		// then
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		assertEquals(1, jobExecution.getStepExecutions().size());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStepExecutions().iterator().next().getStatus());
		
		// Assert that 1000 rows have been inserted
		int count = JdbcTestUtils.countRowsInTable(new JdbcTemplate(context.getBean(DataSource.class)), "ncmmis_provider");
		assertTrue(count == 1000);
		
	}
}
