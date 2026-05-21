package org.ncmmis.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class DataSourceConfig {      
	
    private final String datasourceUrl;
    private final String datasourceUsername;
    private final String datasourcePassword;

    public DataSourceConfig(@Value("${spring.datasource.url}") String datasourceUrl,
    				@Value("${spring.datasource.username}") String datasourceUsername,
    				@Value("${spring.datasource.password}") String datasourcePassword) {
    	
        this.datasourceUrl = datasourceUrl;
        this.datasourceUsername = datasourceUsername;
        this.datasourcePassword = datasourcePassword;
    }

	@Bean
	DataSource dataSource() {
		return DataSourceBuilder.create()
			.url(datasourceUrl)
			.username(datasourceUsername)
			.password(datasourcePassword)
			.build();
	}

	@Bean
	JdbcTransactionManager transactionManager(DataSource dataSource) {
		return new JdbcTransactionManager(dataSource);
	}

}
