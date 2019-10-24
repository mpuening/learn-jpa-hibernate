package io.github.learnjpahibernate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.learnjpahibernate.data.DatasourceProxyBeanPostProcessor;

@Configuration
public class DatasourceProxyConfiguration {
	@Bean
	public DatasourceProxyBeanPostProcessor datasourceProxyBeanPostProcessor() {
		return new DatasourceProxyBeanPostProcessor(true, "Batch-Insert-Logger");
	}
}
