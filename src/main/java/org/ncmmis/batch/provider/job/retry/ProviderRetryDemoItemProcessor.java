package org.ncmmis.batch.provider.job.retry;

import java.util.HashMap;
import java.util.Map;

import org.ncmmis.batch.provider.entity.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderRetryDemoItemProcessor implements ItemProcessor<Provider, Provider> {

	private static final Logger log = LoggerFactory.getLogger(ProviderRetryDemoItemProcessor.class);
	private static final int RETRY_PROVIDER_ID = 602;
	private static final int SUCCESSFUL_ATTEMPT = 3;

	private final Map<Integer, Integer> attemptsByProviderId = new HashMap<>();

	@Override
	public Provider process(Provider provider) {
		int attempt = attemptsByProviderId.merge(provider.getId(), 1, Integer::sum);

		if (provider.getId() == RETRY_PROVIDER_ID && attempt < SUCCESSFUL_ATTEMPT) {
			log.info("Provider id={} failed on attempt {}; retry is configured for this exception.",
					provider.getId(),
					attempt);

			throw new ProviderRetryDemoException(
					"Intentional transient failure for provider id " + provider.getId()
							+ " on attempt " + attempt);
		}

		if (provider.getId() == RETRY_PROVIDER_ID) {
			log.info("Provider id={} processed successfully on attempt {}", provider.getId(), attempt);
		}

		return provider;
	}

	int attemptCount(int providerId) {
		return attemptsByProviderId.getOrDefault(providerId, 0);
	}
}
