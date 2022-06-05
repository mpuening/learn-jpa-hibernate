package io.github.learnjpahibernate.config;

import javax.naming.NamingException;
import jakarta.persistence.EntityManagerFactory;
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
@EnableJpaRepositories(entityManagerFactoryRef = "secondaryEntityManagerFactory", transactionManagerRef = TransactionManagerConfiguration.SECONDARY_TRANSACTION_MANAGER_NAME, basePackages = {
		"io.github.learnjpahibernate.secondary" })
@EnableConfigurationProperties({ SecondaryJpaConfiguration.SecondaryJpaProperties.class })
public class SecondaryJpaConfiguration extends AbstractJpaConfiguration {
	private static final String JPA_PREFIX = "application.secondary.jpa";

	@Autowired
	private SecondaryJpaProperties jpaProperties;

	@Autowired
	@Qualifier("secondaryDataSource")
	private DataSource secondaryDataSource;

	@DependsOn("secondaryDataSourceInitializer")
	@Bean("secondaryEntityManagerFactory")
	public EntityManagerFactory entityManagerFactory() throws IllegalArgumentException, NamingException {
		return buildEntityManagerFactory(secondaryDataSource, jpaProperties, "secondaryPersistenceUnit",
				"io.github.learnjpahibernate.secondary");
	}

	@ConfigurationProperties(prefix = JPA_PREFIX)
	public static class SecondaryJpaProperties extends JpaProperties {
	}
}