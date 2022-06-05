package io.github.learnjpahibernate.activerecord;

import java.io.Serializable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

import io.github.learnjpahibernate.data.AbstractEntity;

@Configurable
public abstract class Jpa1ActiveRecord<PK extends Serializable> extends AbstractEntity<PK> {

	/**
	 * This class provides a default FactoryMethod that relies on AspectJ to set
	 * EntityManager
	 */
	@SuppressWarnings("rawtypes")
	public static class EntityManagerFactoryAspectJSupportMethod implements EntityManagerFactoryMethod {
		public EntityManager entityManagerForClass(Class<?> clazz) {
			EntityManager entityManager = null;
			try {
				entityManager = ((Jpa1ActiveRecord) (clazz.newInstance())).entityManager;
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

	/**
	 * Support a static variable to set the default or overridden FactoryMethod
	 */
	private static EntityManagerFactoryMethod entityManagerFactoryMethod = getDefaultEntityManagerFactoryMethod();

	private static EntityManagerFactoryMethod getDefaultEntityManagerFactoryMethod() {
		return new EntityManagerFactoryAspectJSupportMethod();
	}

	public static void setDefaultEntityManagerFactoryMethod() {
		Jpa1ActiveRecord.entityManagerFactoryMethod = getDefaultEntityManagerFactoryMethod();
	}

	public static void setEntityManagerFactoryMethod(EntityManagerFactoryMethod entityManagerFactoryMethod) {
		Jpa1ActiveRecord.entityManagerFactoryMethod = entityManagerFactoryMethod;
	}

	@PersistenceContext
	transient EntityManager entityManager;

	protected EntityManager entityManager() {
		return entityManagerForClass(getClass());
	}

	protected void assertEntityManager() {
		if (this.entityManager == null) {
			this.entityManager = entityManager();
		}
	}

	protected static final EntityManager entityManagerForClass(Class<?> clazz) {
		return Jpa1ActiveRecord.entityManagerFactoryMethod.entityManagerForClass(clazz);
	}

	public static <T> T find(Class<T> entityClass, Object primaryKey) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey);
		return entity != null ? entityManager.merge(entity) : entity;
	}

	public static <T> T getReference(Class<T> entityClass, Object primaryKey) {
		EntityManager entityManager = entityManagerForClass(entityClass);
		T entity = (T) entityManager.getReference(entityClass, primaryKey);
		return entity;
	}

	public void persist() {
		assertEntityManager();
		this.entityManager.persist(this);
	}

	@SuppressWarnings("unchecked")
	public <T> T merge() {
		assertEntityManager();
		T merged = (T) this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public void remove() {
		assertEntityManager();
		if (this.entityManager.contains(this)) {
			this.entityManager.remove(this);
		} else {
			Object attached = this.entityManager.find(this.getClass(), getId());
			this.entityManager.remove(attached);
		}
	}

	public void flush() {
		assertEntityManager();
		this.entityManager.flush();
	}

	public void refresh() {
		assertEntityManager();
		this.entityManager.refresh(this);
	}

	public void lock(LockModeType lockMode) {
		assertEntityManager();
		this.entityManager.lock(this, lockMode);
	}

	@Transient
	public boolean isContained() {
		assertEntityManager();
		return this.entityManager.contains(this);
	}
}
