package io.github.learnjpahibernate.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({ SecondaryDataSourceConfiguration.SecondaryDataSourceProperties.class })
public class SecondaryDataSourceConfiguration extends AbstractDataSourceConfiguration {
	private static final String DATASOURCE_PREFIX = "application.secondary.datasource";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SecondaryDataSourceProperties secondaryDataSourceProperties;

	@Bean("secondaryDataSource")
	public DataSource secondaryDataSource() throws IllegalArgumentException, NamingException {
		return createDataSource(secondaryDataSourceProperties);
	}

	@Bean("secondaryDataSourceInitializer")
	public DataSourceInitializer secondaryDataSourceInitializer() throws IllegalArgumentException, NamingException {
		return buildDatabasePopulator(applicationContext, secondaryDataSource(), secondaryDataSourceProperties);
	}

	@Bean("secondaryTransactionManager")
	@ConditionalOnMissingClass("org.springframework.data.jpa.domain.Specification")
	public PlatformTransactionManager secondaryTransactionManager() throws IllegalArgumentException, NamingException {
		return new DataSourceTransactionManager(secondaryDataSource());
	}

	@ConfigurationProperties(prefix = DATASOURCE_PREFIX)
	public static class SecondaryDataSourceProperties extends ExtendedDataSourceProperties {
	}
}