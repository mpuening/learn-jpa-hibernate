package io.github.learnjpahibernate.config;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "primaryEntityManagerFactory", transactionManagerRef = TransactionManagerConfiguration.PRIMARY_TRANSACTION_MANAGER_NAME, basePackages = {
		"io.github.learnjpahibernate.primary" })
@EnableConfigurationProperties({ PrimaryJpaConfiguration.PrimaryJpaProperties.class })
public class PrimaryJpaConfiguration extends AbstractJpaConfiguration {
	private static final String JPA_PREFIX = "application.primary.jpa";

	@Autowired
	private PrimaryJpaProperties jpaProperties;

	@Autowired
	@Qualifier("primaryDataSource")
	private DataSource primaryDataSource;

	@DependsOn("primaryDataSourceInitializer")
	@Bean(name = "primaryEntityManagerFactory")
	public EntityManagerFactory entityManagerFactory() throws IllegalArgumentException, NamingException {
		return buildEntityManagerFactory(primaryDataSource, jpaProperties, "primaryPersistenceUnit",
				"io.github.learnjpahibernate.primary");
	}

	@ConfigurationProperties(prefix = JPA_PREFIX)
	public static class PrimaryJpaProperties extends JpaProperties {
	}
}