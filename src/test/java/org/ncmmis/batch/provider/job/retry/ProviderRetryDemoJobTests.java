package org.ncmmis.batch.provider.job.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ncmmis.batch.config.TestDataSourceConfig;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBatchTest
@SpringJUnitConfig({
    ProviderRetryDemoJob.class,
    TestDataSourceConfig.class
})
@Sql(scripts = {
    "/sql/schemas/schema-batch-h2.sql",
    "/sql/schemas/schema-provider-h2.sql"
})
public class ProviderRetryDemoJobTests {

    private final JobOperator jobOperator;
    private final Job job;
    private final ProviderRetryDemoItemProcessor itemProcessor;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ProviderRetryDemoJobTests(
    		JobOperator jobOperator,
    		Job job,
    		ProviderRetryDemoItemProcessor itemProcessor,
    		DataSource dataSource) {

        this.jobOperator = jobOperator;
        this.job = job;
        this.itemProcessor = itemProcessor;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Test
	public void testLaunchProviderRetryDemoJob() throws Exception {

        JobExecution jobExecution = jobOperator.start(job, new JobParameters());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(1, jobExecution.getStepExecutions().size());
        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());

        assertEquals(4, stepExecution.getReadCount());
        assertEquals(0, stepExecution.getFilterCount());
        assertEquals(0, stepExecution.getProcessSkipCount());
        assertEquals(4, stepExecution.getWriteCount());

        assertEquals(3, itemProcessor.attemptCount(602));
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));
        assertEquals(1, countProviderById(602));
	}

	private Integer countProviderById(int id) {
		return jdbcTemplate.queryForObject(
				"select count(*) from ncmmis_provider where id = ?",
				Integer.class,
				id);
	}
}
