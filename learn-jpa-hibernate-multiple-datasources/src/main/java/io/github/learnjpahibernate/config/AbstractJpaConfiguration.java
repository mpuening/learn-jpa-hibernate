package io.github.learnjpahibernate.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

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
		EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(adapter, jpaProperties.getProperties(),
				null);
		AbstractEntityManagerFactoryBean entityManagerFactoryBean = builder.dataSource(dataSource)
				.persistenceUnit(persistenceUnit).packages(packages).build();
		entityManagerFactoryBean.afterPropertiesSet();
		return entityManagerFactoryBean.getObject();
	}
}
