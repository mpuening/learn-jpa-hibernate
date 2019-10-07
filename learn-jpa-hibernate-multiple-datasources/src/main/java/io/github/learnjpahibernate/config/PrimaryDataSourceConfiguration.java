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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({ PrimaryDataSourceConfiguration.PrimaryDataSourceProperties.class })
public class PrimaryDataSourceConfiguration extends AbstractDataSourceConfiguration {
	private static final String DATASOURCE_PREFIX = "application.primary.datasource";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private PrimaryDataSourceProperties primaryDataSourceProperties;

	@Primary
	@Bean(name = { "dataSource", "primaryDataSource" })
	public DataSource primaryDataSource() throws IllegalArgumentException, NamingException {
		return createDataSource(primaryDataSourceProperties);
	}

	@Bean("primaryDataSourceInitializer")
	public DataSourceInitializer primaryDataSourceInitializer() throws IllegalArgumentException, NamingException {
		return buildDatabasePopulator(applicationContext, primaryDataSource(), primaryDataSourceProperties);
	}

	@Primary
	@Bean(name = { "transactionManager", "primaryTransactionManager" })
	@ConditionalOnMissingClass("org.springframework.data.jpa.domain.Specification")
	public PlatformTransactionManager primaryTransactionManager() throws IllegalArgumentException, NamingException {
		return new DataSourceTransactionManager(primaryDataSource());
	}

	@ConfigurationProperties(prefix = DATASOURCE_PREFIX)
	public static class PrimaryDataSourceProperties extends ExtendedDataSourceProperties {
	}
}