package org.ncmmis.batch.provider.job.job2;

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
    ProviderJob2.class,
    TestDataSourceConfig.class
})
@Sql(scripts = {
    "/sql/schemas/schema-batch-h2.sql",
    "/sql/schemas/schema-provider-h2.sql"
})
public class ProviderJob2Tests {
	
    private final JobOperator jobOperator;
    private final Job job;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ProviderJob2Tests(JobOperator jobOperator, Job job, DataSource dataSource) {
        this.jobOperator = jobOperator;
        this.job = job;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
	@Test
	public void testLaunchJob2() throws Exception {
		
		deleteTableData("ncmmis_provider");		
		
        int countAfterDelete = JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider");
        assertEquals(0, countAfterDelete);

        JobExecution jobExecution = jobOperator.start(job, new JobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(1, jobExecution.getStepExecutions().size());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStepExecutions().iterator().next().getStatus());

        int countAfterInsert = JdbcTestUtils.countRowsInTable(jdbcTemplate, "ncmmis_provider");
        assertEquals(1000, countAfterInsert);
	}
	
	private void deleteTableData(String tableName) {		
        JdbcTestUtils.deleteFromTables(jdbcTemplate, tableName);       
        int countAfterDelete = JdbcTestUtils.countRowsInTable(jdbcTemplate, tableName);
        assertEquals(0, countAfterDelete);
	}
}
