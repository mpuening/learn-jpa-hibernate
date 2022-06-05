package io.github.learnjpahibernate.config;

import javax.naming.NamingException;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EclipselinkConfiguration {
	@Autowired
	private DataSource dataSource;

	@Autowired
	private JpaProperties jpaProperties;

	@Bean
	public PlatformTransactionManager transactionManager() throws IllegalArgumentException, NamingException {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory());
		return transactionManager;
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() throws IllegalArgumentException, NamingException {
		return buildEntityManagerFactory(dataSource, jpaProperties, "persistenceUnit",
				"io.github.learnjpahibernate.model");
	}

	protected JpaVendorAdapter getJpaVendorAdapter(JpaProperties jpaProperties) {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		adapter.setShowSql(jpaProperties.isShowSql());
		adapter.setDatabase(jpaProperties.getDatabase());
		adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
		adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
		return adapter;
	}

	protected EntityManagerFactory buildEntityManagerFactory(DataSource dataSource, JpaProperties jpaProperties,
			String persistenceUnit, String... packages) {
		JpaVendorAdapter adapter = getJpaVendorAdapter(jpaProperties);
		EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(adapter, jpaProperties.getProperties(),
				null);
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = builder.dataSource(dataSource)
				.persistenceUnit(persistenceUnit).packages(packages).build();
		localContainerEntityManagerFactoryBean.setJpaDialect(new EclipseLinkJpaDialect());
		localContainerEntityManagerFactoryBean.afterPropertiesSet();
		return localContainerEntityManagerFactoryBean.getObject();
	}
}
