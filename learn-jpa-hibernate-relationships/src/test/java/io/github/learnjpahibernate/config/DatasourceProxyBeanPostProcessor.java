package io.github.learnjpahibernate.config;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;

@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		if (bean instanceof DataSource) {
			DataSource dataSourceBean = (DataSource) bean;
			return new ProxyTestDataSource(dataSourceBean);
		}
		return bean;
	}
}