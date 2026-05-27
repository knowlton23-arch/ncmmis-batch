package org.ncmmis.batch.provider.job.job2;

import org.ncmmis.batch.provider.entity.Provider;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderLoadItemProcessor implements ItemProcessor<Provider, Provider> {

	@Override
	public Provider process(Provider provider) throws Exception {
	
		// System.out.println("Processing provider id: " + provider.getId());
		return provider;
	}
}
