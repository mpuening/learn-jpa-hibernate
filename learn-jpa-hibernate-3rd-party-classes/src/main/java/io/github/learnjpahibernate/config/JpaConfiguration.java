package io.github.learnjpahibernate.config;

import javax.naming.NamingException;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager", basePackages = {
		"io.github.learnjpahibernate.repository" })
@EnableConfigurationProperties({ JpaConfiguration.PersistenceXmlJpaProperties.class })
public class JpaConfiguration {

	private static final String JPA_PREFIX = "spring.jpa";

	@Autowired
	private PersistenceXmlJpaProperties jpaProperties;

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Bean
	public EntityManagerFactory entityManagerFactory() throws IllegalArgumentException, NamingException {
		return buildEntityManagerFactory(dataSource, jpaProperties, "entityManagerFactory", "io.github.learnjpahibernate.model");
	}

	@Primary
	@Bean
	public PlatformTransactionManager transactionManager() throws IllegalArgumentException, NamingException {
		return new JpaTransactionManager(entityManagerFactory());
	}

	@Primary
	@ConfigurationProperties(prefix = JPA_PREFIX)
	public static class PersistenceXmlJpaProperties extends JpaProperties {
	}

	protected JpaVendorAdapter getJpaVendorAdapter(JpaProperties jpaProperties) {
		AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
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
		AbstractEntityManagerFactoryBean entityManagerFactoryBean = builder.dataSource(dataSource)
				// .persistenceUnit(persistenceUnit)
				// .packages(packages)
				.build();
		if (entityManagerFactoryBean instanceof LocalContainerEntityManagerFactoryBean) {
			// Support persistence.xml file
			LocalContainerEntityManagerFactoryBean local = (LocalContainerEntityManagerFactoryBean) entityManagerFactoryBean;
			local.setPersistenceXmlLocation("classpath:/META-INF/persistence.xml");
		}
		entityManagerFactoryBean.afterPropertiesSet();
		return entityManagerFactoryBean.getObject();
	}

}
