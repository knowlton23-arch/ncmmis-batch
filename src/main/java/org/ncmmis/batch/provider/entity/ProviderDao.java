package org.ncmmis.batch.provider.entity;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ProviderDao {

	public static final String INSERT_PROVIDER = "insert into mmis_provider (id, npi, last_name, first_name, ssn, email)"
			+ " values (:id, :npi, :lastName, :firstName, :ssn, :email)";

	private NamedParameterJdbcOperations namedParameterJdbcTemplate;

	public void saveProvider(Provider provider) {
		namedParameterJdbcTemplate.update(INSERT_PROVIDER, new BeanPropertySqlParameterSource(provider));
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
