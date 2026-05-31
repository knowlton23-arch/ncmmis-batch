package org.ncmmis.batch.provider.job.multistep;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    ProviderMultiStepJob.class,
    TestDataSourceConfig.class
})
@Sql(scripts = {
    "/sql/schemas/schema-batch-h2.sql",
    "/sql/schemas/schema-provider-h2.sql"
})
public class ProviderMultiStepJobTests {

    private final JobOperator jobOperator;
    private final Job job;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ProviderMultiStepJobTests(JobOperator jobOperator, Job job, DataSource dataSource) {
        this.jobOperator = jobOperator;
        this.job = job;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Test
	public void testLaunchProviderMultiStepJob() throws Exception {

		jdbcTemplate.update("""
				insert into ncmmis_provider (id, npi, last_name, first_name, ssn, email)
				values (?, ?, ?, ?, ?, ?)
				""",
				9999,
				1999999999L,
				"Stale",
				"Provider",
				"999-99-9999",
				"stale.provider@example.com");

        JobExecution jobExecution = jobOperator.start(job, new JobParameters());
        Map<String, StepExecution> stepExecutions = jobExecution.getStepExecutions()
        		.stream()
        		.collect(Collectors.toMap(StepExecution::getStepName, Function.identity()));

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(3, jobExecution.getStepExecutions().size());

        StepExecution cleanupStepExecution = stepExecutions.get("providerMultiStepCleanupStep");
        StepExecution loadStepExecution = stepExecutions.get("providerMultiStepLoadStep");
        StepExecution summaryStepExecution = stepExecutions.get("providerMultiStepSummaryStep");

        assertEquals(BatchStatus.COMPLETED, cleanupStepExecution.getStatus());
        assertEquals(BatchStatus.COMPLETED, loadStepExecution.getStatus());
        assertEquals(BatchStatus.COMPLETED, summaryStepExecution.getStatus());

        assertEquals(4, loadStepExecution.getReadCount());
        assertEquals(0, loadStepExecution.getFilterCount());
        assertEquals(4, loadStepExecution.getWriteCount());

        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));
        assertEquals(0, countProviderById(9999));
	}

	private Integer countProviderById(int id) {
		return jdbcTemplate.queryForObject(
				"select count(*) from ncmmis_provider where id = ?",
				Integer.class,
				id);
	}
}
