package io.github.learnjpahibernate.activerecord.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Transient;

import io.github.learnjpahibernate.activerecord.aspect.EntityManagerFactoryMixinSupportMethod;

public abstract aspect JpaActiveRecordAspect {

    public interface JpaActiveRecord_internal {
    }

    @PersistenceContext
    transient EntityManager JpaActiveRecord_internal.entityManager;

    public EntityManager JpaActiveRecord_internal.entityManager() {
        return EntityManagerFactoryMixinSupportMethod.getEntityManagerFactoryMethod()
            .entityManagerForClass(getClass());
    }
    
    public EntityManager JpaActiveRecord_internal.internalUseOnly_EntityManager() {
        return entityManager;
    }

    public void JpaActiveRecord_internal.assertEntityManager() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
    }

    public void JpaActiveRecord_internal.persist() {
        assertEntityManager();
        this.entityManager.persist(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T JpaActiveRecord_internal.merge() {
        assertEntityManager();
        T merged = (T) this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public void JpaActiveRecord_internal.remove() {
        assertEntityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        }
        else {
            throw new IllegalStateException(
                "Unable to remove detached Entity and we're unable to determine the Entity Id");
        }
    }

    public void JpaActiveRecord_internal.flush() {
        assertEntityManager();
        this.entityManager.flush();
    }

    public void JpaActiveRecord_internal.refresh() {
        assertEntityManager();
        this.entityManager.refresh(this);
    }

    public void JpaActiveRecord_internal.lock(LockModeType lockMode) {
        assertEntityManager();
        this.entityManager.lock(this, lockMode);
    }

    @Transient
    public boolean JpaActiveRecord_internal.isContained() {
        assertEntityManager();
        return this.entityManager.contains(this);
    }
}
