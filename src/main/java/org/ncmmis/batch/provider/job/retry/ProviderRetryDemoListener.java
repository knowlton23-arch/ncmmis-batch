package org.ncmmis.batch.provider.job.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryState;
import org.springframework.core.retry.Retryable;

public class ProviderRetryDemoListener implements RetryListener {

	private static final Logger log = LoggerFactory.getLogger(ProviderRetryDemoListener.class);

	@Override
	public void onRetryFailure(RetryPolicy retryPolicy, Retryable<?> retryable, Throwable throwable) {
		log.info("Retryable failure: {}", throwable.getMessage());
	}

	@Override
	public void beforeRetry(RetryPolicy retryPolicy, Retryable<?> retryable, RetryState retryState) {
		log.info("Retrying after {} failed attempt(s).", retryState.getRetryCount());
	}

	@Override
	public void onRetrySuccess(RetryPolicy retryPolicy, Retryable<?> retryable, Object result) {
		log.info("Retry completed successfully.");
	}

	@Override
	public void onRetryPolicyExhaustion(
			RetryPolicy retryPolicy,
			Retryable<?> retryable,
			RetryException exception) {

		log.info("Retry policy exhausted: {}", exception.getMessage());
	}
}
