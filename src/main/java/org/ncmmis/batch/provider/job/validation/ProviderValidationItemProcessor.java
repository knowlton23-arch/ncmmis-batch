package org.ncmmis.batch.provider.job.validation;

import org.ncmmis.batch.provider.entity.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ProviderValidationItemProcessor implements ItemProcessor<Provider, Provider> {

	private static final Logger log = LoggerFactory.getLogger(ProviderValidationItemProcessor.class);
	private static final long MIN_TEN_DIGIT_NUMBER = 1_000_000_000L;
	private static final long MAX_TEN_DIGIT_NUMBER = 9_999_999_999L;

	@Override
	public Provider process(Provider provider) {
		String rejectionReason = rejectionReason(provider);

		if (rejectionReason != null) {
			log.info("Filtering provider id={}: {}", provider.getId(), rejectionReason);
			return null;
		}

		return provider;
	}

	private String rejectionReason(Provider provider) {
		if (provider.getId() <= 0) {
			return "id must be greater than 0";
		}

		if (provider.getNpi() < MIN_TEN_DIGIT_NUMBER || provider.getNpi() > MAX_TEN_DIGIT_NUMBER) {
			return "npi must be a 10-digit number";
		}

		if (isBlank(provider.getLastName())) {
			return "lastName must not be blank";
		}

		if (isBlank(provider.getFirstName())) {
			return "firstName must not be blank";
		}

		if (isBlank(provider.getSsn())) {
			return "ssn must not be blank";
		}

		if (isBlank(provider.getEmail()) || !provider.getEmail().contains("@")) {
			return "email must contain @";
		}

		return null;
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
