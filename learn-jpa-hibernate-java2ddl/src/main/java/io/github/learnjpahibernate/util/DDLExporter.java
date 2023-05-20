package io.github.learnjpahibernate.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.tool.schema.spi.DelayedDropRegistryNotAvailableImpl;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 * This class uses amHibernate 5 compatible LocalSessionFactoryBean. Is there one
 * for Hibernate 6? No? In the event it becomes deprecated, check out the following
 * link for ideas for replacement:
 *
 * https://shekhargulati.com/2018/01/09/programmatically-generating-database-schema-with-hibernate-5/
 */
public class DDLExporter {
	public void exportDDL(String packageName, String dialect, String fileName) throws Exception {
		LocalSessionFactoryBean localSessionFactoryBean = createLocalSessionFactoryBean(packageName, dialect);
		StandardServiceRegistry serviceRegistry = localSessionFactoryBean.getConfiguration()
				.getStandardServiceRegistryBuilder().build();

		try {
			String outputFile = fileName;
			Files.deleteIfExists(Paths.get(outputFile));
			MetadataImplementor metadata = createMetadata(localSessionFactoryBean, serviceRegistry);
			
			Map<String, Object> settings = new HashMap<>(serviceRegistry.getService(ConfigurationService.class).getSettings());
			settings.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, "create-drop");
			settings.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_DROP_TARGET, outputFile);
			settings.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_CREATE_TARGET, outputFile);
			settings.put(AvailableSettings.FORMAT_SQL, true);

			SchemaManagementToolCoordinator.process(
					metadata,
					serviceRegistry,
					settings,
					DelayedDropRegistryNotAvailableImpl.INSTANCE);
		} finally {
			StandardServiceRegistryBuilder.destroy(serviceRegistry);
		}
	}

	protected LocalSessionFactoryBean createLocalSessionFactoryBean(String packageName, String dialect)
			throws IOException {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setPackagesToScan(packageName);
		localSessionFactoryBean.setHibernateProperties(createHibernateProperties(dialect));
		localSessionFactoryBean.afterPropertiesSet();

		return localSessionFactoryBean;
	}

	protected Properties createHibernateProperties(String dialect) {
		Properties props = new Properties();
		props.put("hibernate.dialect", dialect);
		props.put("hibernate.show_sql", false);
		props.put("hibernate.hbm2ddl.auto", "none");
		return props;
	}

	protected MetadataImplementor createMetadata(LocalSessionFactoryBean localSessionFactoryBean,
			StandardServiceRegistry registry) throws Exception {
		MetadataSources metadataSources = localSessionFactoryBean.getMetadataSources();
		Metadata metadata = metadataSources.getMetadataBuilder(registry)
				.applyPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl())
				.applyImplicitNamingStrategy(new SpringImplicitNamingStrategy()).build();
		return (MetadataImplementor) metadata;
	}
}
