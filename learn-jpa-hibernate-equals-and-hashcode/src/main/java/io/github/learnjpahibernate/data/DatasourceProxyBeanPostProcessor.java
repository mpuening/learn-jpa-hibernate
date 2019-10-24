package io.github.learnjpahibernate.data;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {
	private final boolean addLoggingInterceptor;
	private final String name;

	public DatasourceProxyBeanPostProcessor() {
		this(false, null);
	}

	public DatasourceProxyBeanPostProcessor(boolean addLoggingInterceptor, String name) {
		this.addLoggingInterceptor = addLoggingInterceptor;
		this.name = name;
	}

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
			if (addLoggingInterceptor) {
				factory.addAdvice(new LoggingDataSourceInterceptor((DataSource) newBean, name));
			}
			return factory.getProxy();
		}
		return bean;
	}

	/**
	 * Interceptor class to log statements with batch size information
	 */
	private static class LoggingDataSourceInterceptor implements MethodInterceptor {

		private final DataSource dataSource;

		public LoggingDataSourceInterceptor(final DataSource dataSource, final String name) {
			// For example: "Batch-Insert-Logger"
			this.dataSource = ProxyDataSourceBuilder.create(dataSource).name(name).asJson()
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
