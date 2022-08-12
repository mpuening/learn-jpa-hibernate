package io.github.learnjpahibernate.activerecord.aspect;

import java.util.Map;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Transient;

public abstract aspect AbstractJpa2ActiveRecordAspect extends AbstractJpa1ActiveRecordAspect {

    public interface AbstractJpa2ActiveRecord_internal extends AbstractJpa1ActiveRecord_internal {
    }

    public void AbstractJpa2ActiveRecord_internal.detach() {
        assertEntityManager();
        this.entityManager.detach(this);
    }

    @Transient
    public LockModeType AbstractJpa2ActiveRecord_internal.getLockMode() {
        assertEntityManager();
        return this.entityManager.getLockMode(this);
    }

    public void AbstractJpa2ActiveRecord_internal.lock(LockModeType lockMode, Map<String, Object> properties) {
        assertEntityManager();
        this.entityManager.lock(this, lockMode, properties);
    }

    public void AbstractJpa2ActiveRecord_internal.refresh(Map<String, Object> properties) {
        assertEntityManager();
        this.entityManager.refresh(this, properties);
    }

    public void AbstractJpa2ActiveRecord_internal.refresh(LockModeType lockMode) {
        assertEntityManager();
        this.entityManager.refresh(this, lockMode);
    }

    public void AbstractJpa2ActiveRecord_internal.refresh(LockModeType lockMode,
        Map<String, Object> properties) {
        assertEntityManager();
        this.entityManager.refresh(this, lockMode, properties);
    }
}
