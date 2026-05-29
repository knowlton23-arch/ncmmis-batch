package org.ncmmis.batch.provider.job.job3;

import org.ncmmis.batch.provider.entity.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderJob3ItemProcessor implements ItemProcessor<Provider, Provider>, StepExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(ProviderJob3ItemProcessor.class);
	private static final int FAILURE_PROVIDER_ID = 350;

	private final JobRepository jobRepository;
	private boolean failThisExecution;

	public ProviderJob3ItemProcessor(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		JobExecution jobExecution = stepExecution.getJobExecution();
		int executionCount = jobRepository.getJobExecutions(jobExecution.getJobInstance()).size();
		failThisExecution = executionCount <= 1;

		log.info(
				"ProviderJob3 restart demo: jobExecutionId={}, executionCount={}, failThisExecution={}",
				jobExecution.getId(),
				executionCount,
				failThisExecution);
	}

	@Override
	public Provider process(Provider provider) {
		if (failThisExecution && provider.getId() == FAILURE_PROVIDER_ID) {
			throw new IllegalStateException(
					"Intentional ProviderJob3 failure at provider id " + FAILURE_PROVIDER_ID);
		}

		return provider;
	}
}
