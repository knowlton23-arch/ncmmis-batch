package org.ncmmis.batch.provider.job.load;

import org.ncmmis.batch.provider.entity.Provider;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderLoadItemProcessor implements ItemProcessor<Provider, Provider> {

	@Override
	public Provider process(Provider provider) throws Exception {
	
		// Business logic goes here
		
		return provider;
	}
}
