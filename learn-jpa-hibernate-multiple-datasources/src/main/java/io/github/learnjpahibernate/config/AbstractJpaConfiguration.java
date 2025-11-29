package io.github.learnjpahibernate.config;

import javax.sql.DataSource;

import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;

public abstract class AbstractJpaConfiguration {
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
		EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
				adapter,
				(ds) -> jpaProperties.getProperties(),
				null);
		AbstractEntityManagerFactoryBean entityManagerFactoryBean = builder.dataSource(dataSource)
				.persistenceUnit(persistenceUnit).packages(packages).build();
		entityManagerFactoryBean.afterPropertiesSet();
		return entityManagerFactoryBean.getObject();
	}
}
