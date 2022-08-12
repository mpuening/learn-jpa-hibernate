package io.github.learnjpahibernate.activerecord.aspect;

import jakarta.persistence.EntityManager;

import io.github.learnjpahibernate.activerecord.EntityManagerFactoryMethod;
import io.github.learnjpahibernate.activerecord.aspect.AbstractJpa1ActiveRecordAspect.AbstractJpa1ActiveRecord_internal;

/**
 * This class provides a default FactoryMethod that relies on AspectJ to set
 * EntityManager
 */
public class EntityManagerFactoryMixinSupportMethod implements EntityManagerFactoryMethod {
	/**
	 * Support a static variable to set the default or overridden FactoryMethod
	 */
	private static EntityManagerFactoryMethod entityManagerFactoryMethod = getDefaultEntityManagerFactoryMethod();

	private static EntityManagerFactoryMethod getDefaultEntityManagerFactoryMethod() {
		return new EntityManagerFactoryMixinSupportMethod();
	}

	public static EntityManagerFactoryMethod getEntityManagerFactoryMethod() {
		return EntityManagerFactoryMixinSupportMethod.entityManagerFactoryMethod;
	}

	public static void setDefaultEntityManagerFactoryMethod() {
		EntityManagerFactoryMixinSupportMethod.entityManagerFactoryMethod = getDefaultEntityManagerFactoryMethod();
	}

	public static void setEntityManagerFactoryMethod(EntityManagerFactoryMethod entityManagerFactoryMethod) {
		EntityManagerFactoryMixinSupportMethod.entityManagerFactoryMethod = entityManagerFactoryMethod;
	}

	public EntityManager entityManagerForClass(Class<?> clazz) {
		EntityManager entityManager = null;
		try {
			entityManager = ((AbstractJpa1ActiveRecord_internal) (clazz.newInstance())).internalUseOnly_EntityManager();
			if (entityManager == null) {
				throw new IllegalStateException(
						"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
			}
		} catch (InstantiationException e) {
			throw new IllegalStateException("Unable to instantiate class: " + clazz.getName(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Illegal access for class: " + clazz.getName(), e);
		}
		return entityManager;
	}
}
