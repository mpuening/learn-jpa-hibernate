package io.github.learnjpahibernate.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

public class DDLExporter {
	public void exportDDL(String packageName, String dialect, String fileName) throws Exception {
		LocalSessionFactoryBean localSessionFactoryBean = createLocalSessionFactoryBean(packageName, dialect);
		StandardServiceRegistry serviceRegistry = localSessionFactoryBean.getConfiguration()
				.getStandardServiceRegistryBuilder().build();

		try {
			String outputFile = fileName;
			Files.deleteIfExists(Paths.get(outputFile));
			MetadataImplementor metadata = createMetadata(localSessionFactoryBean, serviceRegistry);

			SchemaExport export = new SchemaExport();
			export.setDelimiter(";");
			export.setFormat(true);
			export.setOutputFile(outputFile);
			export.create(EnumSet.of(TargetType.SCRIPT), metadata);
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

	@SuppressWarnings("deprecation")
	protected MetadataImplementor createMetadata(LocalSessionFactoryBean localSessionFactoryBean,
			StandardServiceRegistry registry) throws Exception {
		MetadataSources metadataSources = localSessionFactoryBean.getMetadataSources();
		Metadata metadata = metadataSources.getMetadataBuilder(registry)
				.applyPhysicalNamingStrategy(new SpringPhysicalNamingStrategy())
				.applyImplicitNamingStrategy(new SpringImplicitNamingStrategy()).build();
		return (MetadataImplementor) metadata;
	}
}
