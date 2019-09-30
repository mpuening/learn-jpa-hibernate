package io.github.learnjpahibernate.activerecord;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

public abstract class Jpa2ActiveRecord<PK extends Serializable> extends Jpa1ActiveRecord<PK> {

	public static <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey, properties);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey, lockMode);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode,
			Map<String, Object> properties) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey, lockMode, properties);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> TypedQuery<T> createQuery(String query, Class<T> entityClass) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		return entityManager.createQuery(query, entityClass);
	}

	public static <T> TypedQuery<T> createNamedQuery(String name, Class<T> entityClass) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		return entityManager.createNamedQuery(name, entityClass);
	}

	public static <T> Query createNativeQuery(String query, Class<T> entityClass) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		return entityManager.createNativeQuery(query, entityClass);
	}

	public void detach() {
		assertEntityManager();
		this.entityManager.detach(this);
	}

	@Transient
	public LockModeType getLockMode() {
		assertEntityManager();
		return this.entityManager.getLockMode(this);
	}

	public void lock(LockModeType lockMode, Map<String, Object> properties) {
		assertEntityManager();
		this.entityManager.lock(this, lockMode, properties);
	}

	public void refresh(Map<String, Object> properties) {
		assertEntityManager();
		this.entityManager.refresh(this, properties);
	}

	public void refresh(LockModeType lockMode) {
		assertEntityManager();
		this.entityManager.refresh(this, lockMode);
	}

	public void refresh(LockModeType lockMode, Map<String, Object> properties) {
		assertEntityManager();
		this.entityManager.refresh(this, lockMode, properties);
	}
}
