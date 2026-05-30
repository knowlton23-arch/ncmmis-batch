package org.ncmmis.batch.provider.job.skip;

import org.ncmmis.batch.provider.entity.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;

public class ProviderSkipDemoSkipListener implements SkipListener<Provider, Provider> {

	private static final Logger log = LoggerFactory.getLogger(ProviderSkipDemoSkipListener.class);

	@Override
	public void onSkipInProcess(Provider provider, Throwable throwable) {
		log.info("Skipping provider id={}: {}", provider.getId(), throwable.getMessage());
	}
}
