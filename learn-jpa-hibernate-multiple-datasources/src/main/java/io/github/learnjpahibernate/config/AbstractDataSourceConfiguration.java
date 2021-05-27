package io.github.learnjpahibernate.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;

public abstract class AbstractDataSourceConfiguration {

	protected abstract boolean isSqlInitEnabled();

	protected DataSource createDataSource(ExtendedDataSourceProperties dataSourceProperties)
			throws IllegalArgumentException, NamingException {
		if (dataSourceProperties.getJndiName() != null) {
			return lookupDataSource(dataSourceProperties.getJndiName());
		} else if (dataSourceProperties.getEmbeddedDatabaseType() != null) {
			EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
			builder.setType(dataSourceProperties.getEmbeddedDatabaseType());
			EmbeddedDatabase db = builder.build();
			return db;
		} else {
			return dataSourceProperties.initializeDataSourceBuilder().build();
		}
	}

	protected DataSource lookupDataSource(String jndiName) throws NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName(jndiName);
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource) bean.getObject();
	}

	protected Resource[] getResources(ApplicationContext applicationContext, List<String> locationList) {
		List<Resource> resources = new ArrayList<Resource>();
		if (locationList != null) {
			for (String locationFromList : locationList) {
				for (String location : StringUtils.commaDelimitedListToStringArray(locationFromList)) {
					try {
						for (Resource resource : applicationContext.getResources(location)) {
							if (resource.exists()) {
								resources.add(resource);
							}
						}
					} catch (IOException ex) {
						throw new IllegalStateException("Unable to load resource from " + location, ex);
					}
				}
			}
		}
		return resources.toArray(new Resource[resources.size()]);
	}

	protected DataSourceInitializer buildDatabasePopulator(ApplicationContext applicationContext, DataSource dataSource,
			SqlInitializationProperties sqlInitializationProperties) throws NamingException {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.setContinueOnError(sqlInitializationProperties.isContinueOnError());
		databasePopulator.setSeparator(sqlInitializationProperties.getSeparator());
		databasePopulator.setSqlScriptEncoding(
				sqlInitializationProperties.getEncoding() != null ? sqlInitializationProperties.getEncoding().name()
						: null);
		databasePopulator.addScripts(getResources(applicationContext, sqlInitializationProperties.getSchemaLocations()));
		databasePopulator.addScripts(getResources(applicationContext, sqlInitializationProperties.getDataLocations()));
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource);
		dataSourceInitializer.setDatabasePopulator(databasePopulator);
		dataSourceInitializer.setEnabled(isSqlInitEnabled());
		return dataSourceInitializer;
	}

	/**
	 * Extension to support embedded database type to help control how to build data
	 * sources. This helps configure certain embedded databases (e.g. H2).
	 */
	public static abstract class ExtendedDataSourceProperties extends DataSourceProperties {
		private EmbeddedDatabaseType embeddedDatabaseType;

		public EmbeddedDatabaseType getEmbeddedDatabaseType() {
			return embeddedDatabaseType;
		}

		public void setEmbeddedDatabaseType(EmbeddedDatabaseType embeddedDatabaseType) {
			this.embeddedDatabaseType = embeddedDatabaseType;
		}
	}
}
