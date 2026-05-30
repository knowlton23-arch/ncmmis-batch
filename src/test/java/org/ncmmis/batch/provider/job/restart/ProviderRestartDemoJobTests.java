package org.ncmmis.batch.provider.job.restart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ncmmis.batch.config.TestDataSourceConfig;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBatchTest
@SpringJUnitConfig({
    ProviderRestartDemoJob.class,
    TestDataSourceConfig.class
})
@Sql(scripts = {
    "/sql/schemas/schema-batch-h2.sql",
    "/sql/schemas/schema-provider-h2.sql"
})
public class ProviderRestartDemoJobTests {

    private final JobOperator jobOperator;
    private final Job job;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ProviderRestartDemoJobTests(JobOperator jobOperator, Job job, DataSource dataSource) {
        this.jobOperator = jobOperator;
        this.job = job;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Test
	public void testRestartDemoJob() throws Exception {
		JobExecution failedExecution = jobOperator.start(job, new JobParameters());

		assertEquals(BatchStatus.FAILED, failedExecution.getStatus());
		assertEquals(300, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));

		JobExecution restartedExecution = jobOperator.restart(failedExecution);

		assertEquals(BatchStatus.COMPLETED, restartedExecution.getStatus());
		assertEquals(1000, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));
	}
}
