package io.github.learnjpahibernate.activerecord;

import javax.persistence.EntityManager;

/**
 * Factory method to allow configurable means to access the EntityManager.
 * Mocking is an example of one means to get an EntityManager besides default
 * AspectJ.
 */
public interface EntityManagerFactoryMethod {
	EntityManager entityManagerForClass(Class<?> clazz);
}
