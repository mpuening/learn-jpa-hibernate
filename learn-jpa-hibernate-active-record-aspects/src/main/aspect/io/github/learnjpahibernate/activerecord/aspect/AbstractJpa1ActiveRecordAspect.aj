package io.github.learnjpahibernate.activerecord.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Transient;

import io.github.learnjpahibernate.activerecord.aspect.EntityManagerFactoryMixinSupportMethod;

public abstract aspect AbstractJpa1ActiveRecordAspect {

    public interface AbstractJpa1ActiveRecord_internal {
    }

    @PersistenceContext
    transient EntityManager AbstractJpa1ActiveRecord_internal.entityManager;

    public EntityManager AbstractJpa1ActiveRecord_internal.entityManager() {
        return EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
            .entityManagerForClass(getClass());
    }
    
    public EntityManager AbstractJpa1ActiveRecord_internal.internalUseOnly_EntityManager() {
        return entityManager;
    }

    public void AbstractJpa1ActiveRecord_internal.assertEntityManager() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
    }

    public void AbstractJpa1ActiveRecord_internal.persist() {
        assertEntityManager();
        this.entityManager.persist(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T AbstractJpa1ActiveRecord_internal.merge() {
        assertEntityManager();
        T merged = (T) this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public void AbstractJpa1ActiveRecord_internal.remove() {
        assertEntityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        }
        else {
            throw new IllegalStateException(
                "Unable to remove detached Entity and we're unable to determine the Entity Id");
        }
    }

    public void AbstractJpa1ActiveRecord_internal.flush() {
        assertEntityManager();
        this.entityManager.flush();
    }

    public void AbstractJpa1ActiveRecord_internal.refresh() {
        assertEntityManager();
        this.entityManager.refresh(this);
    }

    public void AbstractJpa1ActiveRecord_internal.lock(LockModeType lockMode) {
        assertEntityManager();
        this.entityManager.lock(this, lockMode);
    }

    @Transient
    public boolean AbstractJpa1ActiveRecord_internal.isContained() {
        assertEntityManager();
        return this.entityManager.contains(this);
    }
}
