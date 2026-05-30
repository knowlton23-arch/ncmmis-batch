package org.ncmmis.batch.provider.job.filter;

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
    ProviderFilterJob.class,
    TestDataSourceConfig.class
})
@Sql(scripts = {
    "/sql/schemas/schema-batch-h2.sql",
    "/sql/schemas/schema-provider-h2.sql"
})
public class ProviderFilterJobTests {

    private final JobOperator jobOperator;
    private final Job job;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ProviderFilterJobTests(JobOperator jobOperator, Job job, DataSource dataSource) {
        this.jobOperator = jobOperator;
        this.job = job;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Test
	public void testLaunchProviderFilterJob() throws Exception {

        JobExecution jobExecution = jobOperator.start(job, new JobParameters());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(1, jobExecution.getStepExecutions().size());
        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());

        assertEquals(10, stepExecution.getReadCount());
        assertEquals(5, stepExecution.getFilterCount());
        assertEquals(5, stepExecution.getWriteCount());

        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));
        assertEquals(0, countProviderById(402));
        assertEquals(0, countProviderById(403));
        assertEquals(0, countProviderById(404));
        assertEquals(0, countProviderById(406));
        assertEquals(0, countProviderById(408));
	}

	private Integer countProviderById(int id) {
		return jdbcTemplate.queryForObject(
				"select count(*) from ncmmis_provider where id = ?",
				Integer.class,
				id);
	}
}
