package org.ncmmis.batch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class CustomStepExecutionListener implements StepExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(CustomStepExecutionListener.class);

	@Override
    public void beforeStep(StepExecution stepExecution) {
		log.info("Before step...");
	}
	
	@Override
    public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("After step");
		log.info("Step Summary: {}" + stepExecution.getSummary().toString());
		return stepExecution.getExitStatus();
	}
}
