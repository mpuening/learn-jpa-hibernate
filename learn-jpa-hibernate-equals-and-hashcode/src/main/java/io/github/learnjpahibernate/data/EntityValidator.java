package io.github.learnjpahibernate.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * A utility class to test if an entity bean is valid. It is Spring Boot
 * specific. Inspired from
 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
 */
public class EntityValidator<T extends Persistable<ID>, ID extends Serializable> {

	private final T testEntity;
	private final JpaRepository<T, ID> repository;

	private TransactionTemplate transactionTemplate;
	private boolean skipUpdate = false;
	private Consumer<T> beforeUpdate;
	private boolean skipDelete = false;
	private Consumer<T> beforeDelete;

	/**
	 * @param entity     Pass in an unsaved entity to put through the paces
	 * @param repository Spring JPA Repository
	 */
	public static <T extends Persistable<ID>, ID extends Serializable> EntityValidator<T, ID> with(T newEntity,
			JpaRepository<T, ID> repository) {
		return new EntityValidator<>(newEntity, repository);
	}

	/**
	 * @param id         Pass in an ID to existing entity to put through the paces
	 * @param repository Spring JPA Repository
	 */
	public static <T extends Persistable<ID>, ID extends Serializable> EntityValidator<T, ID> with(ID id,
			JpaRepository<T, ID> repository) {
		return new EntityValidator<>(id, repository);
	}

	/**
	 * @param id         Pass in an ID to existing entity to put through the paces
	 * @param repository Spring JPA Repository
	 */
	protected EntityValidator(ID id, JpaRepository<T, ID> repository) {
		this.testEntity = repository.findById(id).orElse(null);
		assertTrue(this.testEntity != null, "No test entity found for id: " + id);
		this.repository = repository;
	}

	/**
	 * @param entity     Pass in an unsaved entity to put through the paces
	 * @param repository Spring JPA Repository
	 */
	protected EntityValidator(T newEntity, JpaRepository<T, ID> repository) {
		this.testEntity = newEntity;
		this.repository = repository;
	}

	/**
	 * @param transationTemplate TransactionTemplate to run transactions
	 */
	public EntityValidator<T, ID> withTransactions(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
		return this;
	}

	/**
	 * @param skipUpdate Indicate if update method should be skipped
	 */
	public EntityValidator<T, ID> skipUpdate(boolean skipUpdate) {
		this.skipUpdate = skipUpdate;
		return this;
	}

	/**
	 * @param beforeUpdate Lambda expression used to make changes to an entity bean
	 *                     before it is updated in the database. Typical uses would
	 *                     be to update properties or add other entities to an
	 *                     association.
	 */
	public EntityValidator<T, ID> beforeUpdate(Consumer<T> beforeUpdate) {
		this.beforeUpdate = beforeUpdate;
		return this;
	}

	/**
	 * @param skipDelete Indicate if delete method should be skipped
	 */
	public EntityValidator<T, ID> skipDelete(boolean skipDelete) {
		this.skipDelete = skipDelete;
		return this;
	}

	/**
	 * @param beforeDelete Lambda expression used to make changes to an entity bean
	 *                     before it is deleted from the database. Typical uses
	 *                     would be to bulk delete objects from an association.
	 */
	public EntityValidator<T, ID> beforeDelete(Consumer<T> beforeDelete) {
		this.beforeDelete = beforeDelete;
		return this;
	}

	/**
	 * Entities with database generated Ids are allowed to change equality after
	 * being persisted. This method assumes that if the entity being passed in has
	 * an Id, then it is a natural Id, and will not change equality.
	 * 
	 * This method test if an entity can go through the JPA paces.
	 * 
	 * @param entity       Pass in an unsaved entity to put through the paces
	 * @param repository   Spring JPA Repository
	 * @param beforeUpdate Lambda expression used to change a value on an entity
	 *                     bean
	 */
	public void assertEntityIsValid() {

		Set<T> set = new HashSet<>();
		boolean hasExistingId = testEntity.getId() != null;

		doInTransaction((x) -> {
			assertEntityCanBeAddedToSet(set);
		});
		doInTransaction((x) -> {
			assertEntityCanBeSaved(set, hasExistingId);
		});
		doInTransaction((x) -> {
			assertEntityCanBeFound();
		});
		if (!skipUpdate) {
			doInTransaction((x) -> {
				assertEntityCanBeUpdated(set);
			});
		}
		doInTransaction((x) -> {
			assertEntityCanBeFoundAgain(set);
		});
		if (!skipDelete) {
			doInTransaction((x) -> {
				assertEntityCanBeDeleted(set);
			});
		}
	}

	protected void doInTransaction(Consumer<?> assertFunction) {
		if (transactionTemplate != null) {
			transactionTemplate.execute((status) -> {
				assertFunction.accept(null);
				status.flush();
				return null;
			});
		} else {
			assertFunction.accept(null);
		}
	}

	protected void assertEntityCanBeAddedToSet(Set<T> set) {
		assertTrue(!set.contains(testEntity), "Somehow the entity magically got into the set (1)");
		set.add(testEntity);
		assertTrue(set.contains(testEntity), "Somehow the entity cannot be added to a set (1)");
	}

	protected void assertEntityCanBeSaved(Set<T> set, boolean hasExistingId) {
		T entity = repository.saveAndFlush(this.testEntity);
		assertTrue(entity != null, "The recently saved entity was not returned (2)");
		assertTrue(testEntity.getId() != null, "The new entity didn't get an id after being saved (2)");
		assertTrue(entity.equals(testEntity), "The new entity does not equal the recently saved entity (2)");
		assertTrue(testEntity.equals(entity), "The recently saved entity does not equal the new entity (2)");

		if (hasExistingId) {
			assertTrue(set.contains(testEntity), "The entity is not found in the set after being saved (2)");
		} else {
			assertTrue(!set.contains(testEntity),
					"The entity is still found in the set after being saved, even though it now has a new id (2)");
			set.clear();
			set.add(testEntity);
			assertTrue(set.contains(testEntity), "Somehow the entity cannot be added to a set (2)");
		}
	}

	protected void assertEntityCanBeFound() {
		T entity = repository.getById(testEntity.getId());
		assertTrue(entity != null, "The recently saved entity could not be found (3)");
		assertTrue(entity.equals(testEntity), "The queried entity does not equal the recently saved entity (3)");
		assertTrue(testEntity.equals(entity), "The recently saved entity does not equal the queried entity (3)");
	}

	protected void assertEntityCanBeUpdated(Set<T> set) {
		if (beforeUpdate != null) {
			// Make sure entity gets changed
			beforeUpdate.accept(testEntity);
		}
		T entity = repository.saveAndFlush(testEntity);
		assertTrue(set.contains(entity), "The recently saved entity could not be found after update/save (4)");
	}

	protected void assertEntityCanBeFoundAgain(Set<T> set) {
		T entity = repository.findById(testEntity.getId())
				.orElseThrow(() -> new RuntimeException("The recently saved entity could not be found (5)"));
		assertTrue(entity.equals(testEntity), "The queried entity does not equal the recently saved entity (5)");
		assertTrue(testEntity.equals(entity), "The recently saved entity does not equal the queried entity (5)");
		assertTrue(set.contains(entity), "The entity is not found in the set after being queried (5)");
	}

	protected void assertEntityCanBeDeleted(Set<T> set) {
		if (beforeDelete != null) {
			// Make sure entity gets chance to do something before delete
			beforeDelete.accept(testEntity);
		}
		repository.delete(testEntity);
		assertTrue(set.contains(testEntity), "The entity is not found in the set after being deleted (6)");
		T entity = null;
		try {
			entity = repository.getById(testEntity.getId());
		} catch (JpaObjectRetrievalFailureException thisIsExpected) {
			assertTrue(
					thisIsExpected.getCause().getClass().getName().equals("javax.persistence.EntityNotFoundException"),
					"The wrong type of exception occurred while querying deleted entity (6)");
		}
		assertTrue(entity == null, "The deleted entity can still be queried (6)");
	}

	protected void assertTrue(boolean assertion, String message) {
		if (!assertion) {
			throw new RuntimeException(message);
		}
	}
}
