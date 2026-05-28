package org.ncmmis.batch.common;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class CustomStepExecutionListener implements StepExecutionListener {

	@Override
    public void beforeStep(StepExecution stepExecution) {
		System.out.println(">> Before step");
	}
	
	@Override
    public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println(">> After step");
		System.out.println(">> Step Summary: " + stepExecution.getSummary().toString());
		return stepExecution.getExitStatus();
	}
}
