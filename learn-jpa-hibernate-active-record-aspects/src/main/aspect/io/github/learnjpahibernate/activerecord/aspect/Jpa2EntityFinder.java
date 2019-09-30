package io.github.learnjpahibernate.activerecord.aspect;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class Jpa2EntityFinder extends Jpa1EntityFinder {
	public static <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey, properties);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey, lockMode);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode,
			Map<String, Object> properties) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey, lockMode, properties);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> TypedQuery<T> createQuery(String query, Class<T> entityClass) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		return entityManager.createQuery(query, entityClass);
	}

	public static <T> TypedQuery<T> createNamedQuery(String name, Class<T> entityClass) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		return entityManager.createNamedQuery(name, entityClass);
	}

	public static <T> Query createNativeQuery(String query, Class<T> entityClass) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		return entityManager.createNativeQuery(query, entityClass);
	}
}
