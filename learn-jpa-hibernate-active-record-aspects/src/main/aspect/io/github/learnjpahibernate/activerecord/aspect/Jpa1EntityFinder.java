package io.github.learnjpahibernate.activerecord.aspect;

import jakarta.persistence.EntityManager;

public class Jpa1EntityFinder {
	public static <T> T find(Class<T> entityClass, Object primaryKey) {
		EntityManager entityManager = EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
				.entityManagerForClass(entityClass);
		T entity = (T) entityManager.find(entityClass, primaryKey);
		return entity != null ? entityManager.merge(entity) : entity;
	}
}