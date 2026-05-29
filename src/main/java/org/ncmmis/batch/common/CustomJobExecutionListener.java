package org.ncmmis.batch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomJobExecutionListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(CustomJobExecutionListener.class);
	
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Before job: jobName={}, jobExecutionId={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId());
        log.debug("Job execution context: {}", jobExecution.getExecutionContext());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("After job: jobName={}, status={}, exitStatus={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                jobExecution.getExitStatus());
        log.debug("Job execution context: {}", jobExecution.getExecutionContext());
    }
}
