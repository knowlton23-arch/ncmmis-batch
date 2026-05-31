package org.ncmmis.batch.provider.job.conditional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
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
    ProviderConditionalFlowJob.class,
    TestDataSourceConfig.class
})
@Sql(scripts = {
    "/sql/schemas/schema-batch-h2.sql",
    "/sql/schemas/schema-provider-h2.sql"
})
public class ProviderConditionalFlowJobTests {

    private final JobOperator jobOperator;
    private final Job job;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ProviderConditionalFlowJobTests(JobOperator jobOperator, Job job, DataSource dataSource) {
        this.jobOperator = jobOperator;
        this.job = job;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Test
	public void testLaunchProviderConditionalFlowJobWithValidInput() throws Exception {

		JobExecution jobExecution = jobOperator.start(job, jobParameters("valid"));
        Map<String, StepExecution> stepExecutions = stepExecutionsByName(jobExecution);

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(3, jobExecution.getStepExecutions().size());
        assertTrue(stepExecutions.containsKey("providerConditionalValidationStep"));
        assertTrue(stepExecutions.containsKey("providerConditionalLoadStep"));
        assertTrue(stepExecutions.containsKey("providerConditionalLoadedSummaryStep"));
        assertFalse(stepExecutions.containsKey("providerConditionalEmptySummaryStep"));

        StepExecution validationStepExecution = stepExecutions.get("providerConditionalValidationStep");
        StepExecution loadStepExecution = stepExecutions.get("providerConditionalLoadStep");
        StepExecution loadedSummaryStepExecution = stepExecutions.get("providerConditionalLoadedSummaryStep");

        assertEquals(BatchStatus.COMPLETED, validationStepExecution.getStatus());
        assertEquals(ProviderConditionalFlowJob.VALID_DATA, validationStepExecution.getExitStatus().getExitCode());
        assertEquals(BatchStatus.COMPLETED, loadStepExecution.getStatus());
        assertEquals(BatchStatus.COMPLETED, loadedSummaryStepExecution.getStatus());

        assertEquals(4, loadStepExecution.getReadCount());
        assertEquals(0, loadStepExecution.getFilterCount());
        assertEquals(4, loadStepExecution.getWriteCount());
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));
	}

	@Test
	public void testLaunchProviderConditionalFlowJobWithEmptyInput() throws Exception {

		JobExecution jobExecution = jobOperator.start(job, jobParameters("empty"));
        Map<String, StepExecution> stepExecutions = stepExecutionsByName(jobExecution);

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(2, jobExecution.getStepExecutions().size());
        assertTrue(stepExecutions.containsKey("providerConditionalValidationStep"));
        assertFalse(stepExecutions.containsKey("providerConditionalLoadStep"));
        assertFalse(stepExecutions.containsKey("providerConditionalLoadedSummaryStep"));
        assertTrue(stepExecutions.containsKey("providerConditionalEmptySummaryStep"));

        StepExecution validationStepExecution = stepExecutions.get("providerConditionalValidationStep");
        StepExecution emptySummaryStepExecution = stepExecutions.get("providerConditionalEmptySummaryStep");

        assertEquals(BatchStatus.COMPLETED, validationStepExecution.getStatus());
        assertEquals(ProviderConditionalFlowJob.EMPTY_DATA, validationStepExecution.getExitStatus().getExitCode());
        assertEquals(BatchStatus.COMPLETED, emptySummaryStepExecution.getStatus());
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider"));
	}

	private JobParameters jobParameters(String inputMode) {
		return new JobParametersBuilder()
				.addString("inputMode", inputMode)
				.toJobParameters();
	}

	private Map<String, StepExecution> stepExecutionsByName(JobExecution jobExecution) {
		return jobExecution.getStepExecutions()
        		.stream()
        		.collect(Collectors.toMap(StepExecution::getStepName, Function.identity()));
	}
}
