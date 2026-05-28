package org.ncmmis.batch.common;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomJobExecutionListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println(">> Before job");
		System.out.println(">> Job Execution Context: " + jobExecution.getExecutionContext().toString());
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println(">> After job");
		System.out.println(">> Job Execution Context: " + jobExecution.getExecutionContext().toString());
	}
}
