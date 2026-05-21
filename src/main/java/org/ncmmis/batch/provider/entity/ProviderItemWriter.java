package org.ncmmis.batch.provider.entity;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;


public class ProviderItemWriter implements ItemWriter<Provider> {

	private ProviderDao providerDao;

	@Override
	public void write(Chunk<? extends Provider> providers) throws Exception {
		for (Provider provider : providers) {
			providerDao.saveProvider(provider);
		}
	}

	public void setProviderDao(ProviderDao providerDao) {
		this.providerDao = providerDao;
	}

}
