package io.github.learnjpahibernate.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

/**
 * A utility class to test if an entity bean is valid. It is Spring Boot
 * specific.
 */
public class EntityValidator {

	/**
	 * Inspired from
	 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
	 * 
	 * Entities with database generated Ids are allowed to changed equality after
	 * being persisted. This method assumes that if the entity being passed in has
	 * an Id, then it is a natural Id, and will not change equality.
	 * 
	 * This method test if an entity can go through the JPA paces.
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param entity     Pass in an unsaved entity to put through the paces
	 * @param repository Spring JPA Repository
	 * @param updateCode Lambda expression used to change a value on an entity bean
	 */
	public static <T extends Persistable<ID>, ID extends Serializable> void assertEntityIsValid(T newEntity,
			JpaRepository<T, ID> repository, Consumer<T> updateCode) {

		Set<T> set = new HashSet<>();
		boolean isNaturalEntity = newEntity.getId() != null;

		assertTrue(!set.contains(newEntity), "Somehow the entity magically got into the set (1)");
		set.add(newEntity);
		assertTrue(set.contains(newEntity), "Somehow the entity cannot be added to a set (1)");

		repository.saveAndFlush(newEntity);
		assertTrue(newEntity.getId() != null, "The new entity didn't get an id after being saved (2)");
		if (isNaturalEntity) {
			assertTrue(set.contains(newEntity), "The entity is not found in the set after being saved (2)");
		} else {
			assertTrue(!set.contains(newEntity),
					"The entity is still found in the set after being saved, even though it now has a new id (2)");
			set.clear();
			set.add(newEntity);
			assertTrue(set.contains(newEntity), "Somehow the entity cannot be added to a set (2)");
		}

		T entity = repository.getOne(newEntity.getId());
		assertTrue(entity != null, "The recently saved entity could not be found (3)");
		assertTrue(entity.equals(newEntity), "The queried entity does not equal the recently saved entity (3)");
		assertTrue(newEntity.equals(entity), "The recently saved entity does not equal the queried entity (3)");

		// Make sure entity gets changed
		updateCode.accept(newEntity);
		entity = repository.saveAndFlush(newEntity);
		assertTrue(set.contains(entity), "The recently saved entity could not be found after update/save (4)");

		entity = repository.findById(newEntity.getId())
				.orElseThrow(() -> new RuntimeException("The recently saved entity could not be found (5)"));
		assertTrue(entity.equals(newEntity), "The queried entity does not equal the recently saved entity (5)");
		assertTrue(newEntity.equals(entity), "The recently saved entity does not equal the queried entity (5)");
		assertTrue(set.contains(entity), "The entity is not found in the set after being queried (5)");

		repository.delete(entity);
		assertTrue(set.contains(entity), "The entity is not found in the set after being deleted (6)");
		try {
			entity = repository.getOne(newEntity.getId());
		} catch (JpaObjectRetrievalFailureException thisIsExpected) {
			assertTrue(
					thisIsExpected.getCause().getClass().getName().equals("javax.persistence.EntityNotFoundException"),
					"The wrong type of exception occurred while querying deleted entity (6)");
			entity = null;
		}
		assertTrue(entity == null, "The deleted entity can still be queried (6)");
	}

	private static void assertTrue(boolean assertion, String message) {
		if (!assertion) {
			throw new RuntimeException(message);
		}
	}
}
