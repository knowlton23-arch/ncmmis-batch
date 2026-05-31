package org.ncmmis.batch.provider.job.multistep;

import org.ncmmis.batch.provider.entity.Provider;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderMultiStepItemProcessor implements ItemProcessor<Provider, Provider> {

	@Override
	public Provider process(Provider provider) throws Exception {
		return provider;
	}
}
