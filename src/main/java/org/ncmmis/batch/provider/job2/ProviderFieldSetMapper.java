package org.ncmmis.batch.provider.job2;

import org.jspecify.annotations.Nullable;
import org.ncmmis.batch.provider.entity.Provider;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;

public class ProviderFieldSetMapper implements FieldSetMapper<Provider> {

	@Override
	public @Nullable Provider mapFieldSet(FieldSet fs) {

		if (fs == null) {
			return null;
		}

		Provider provider = new Provider();
		provider.setId(fs.readInt("id"));
		provider.setNpi(fs.readInt("npi"));
		provider.setLastName(fs.readString("lastName"));
		provider.setFirstName(fs.readString("firstName"));
		provider.setSsn(fs.readString("ssn"));
		provider.setEmail(fs.readString("email"));
		
		return provider;
	}
}
