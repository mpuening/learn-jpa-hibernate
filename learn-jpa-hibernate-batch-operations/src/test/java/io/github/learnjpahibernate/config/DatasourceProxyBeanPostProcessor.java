package io.github.learnjpahibernate.config;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

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
			ProxyTestDataSource newBean = new ProxyTestDataSource(dataSourceBean);

			ProxyFactory factory = new ProxyFactory(newBean);
			factory.setProxyTargetClass(true);
			factory.addAdvice(new LoggingDataSourceInterceptor((DataSource) newBean));
			return factory.getProxy();
		}
		return bean;
	}

	/**
	 * Interceptor class to log statements with batch size information
	 */
	private static class LoggingDataSourceInterceptor implements MethodInterceptor {

		private final DataSource dataSource;

		public LoggingDataSourceInterceptor(final DataSource dataSource) {
			this.dataSource = ProxyDataSourceBuilder.create(dataSource).name("Batch-Insert-Logger").asJson()
					.countQuery().logQueryToSysOut().build();
		}

		@Override
		public Object invoke(final MethodInvocation invocation) throws Throwable {
			Method proxyMethod = ReflectionUtils.findMethod(dataSource.getClass(), invocation.getMethod().getName());
			if (proxyMethod != null) {
				return proxyMethod.invoke(dataSource, invocation.getArguments());
			}
			return invocation.proceed();
		}
	}
}