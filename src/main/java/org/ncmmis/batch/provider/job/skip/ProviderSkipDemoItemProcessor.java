package org.ncmmis.batch.provider.job.skip;

import org.ncmmis.batch.provider.entity.Provider;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderSkipDemoItemProcessor implements ItemProcessor<Provider, Provider> {

	private static final String SKIPPABLE_LAST_NAME = "SkipMe";

	@Override
	public Provider process(Provider provider) {
		if (SKIPPABLE_LAST_NAME.equals(provider.getLastName())) {
			throw new ProviderSkipDemoException(
					"Intentional ProviderSkipDemoJob skip for provider id " + provider.getId());
		}

		return provider;
	}
}
