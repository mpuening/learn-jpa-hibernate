package io.github.learnjpahibernate.activerecord.aspect;

import java.util.Map;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

public aspect Jpa2ActiveRecordAspect extends JpaActiveRecordAspect {

    public interface Jpa2ActiveRecord_internal extends JpaActiveRecord_internal {
    }

    public void Jpa2ActiveRecord_internal.detach() {
        assertEntityManager();
        this.entityManager.detach(this);
    }

    @Transient
    public LockModeType Jpa2ActiveRecord_internal.getLockMode() {
        assertEntityManager();
        return this.entityManager.getLockMode(this);
    }

    public void Jpa2ActiveRecord_internal.lock(LockModeType lockMode, Map<String, Object> properties) {
        assertEntityManager();
        this.entityManager.lock(this, lockMode, properties);
    }

    public void Jpa2ActiveRecord_internal.refresh(Map<String, Object> properties) {
        assertEntityManager();
        this.entityManager.refresh(this, properties);
    }

    public void Jpa2ActiveRecord_internal.refresh(LockModeType lockMode) {
        assertEntityManager();
        this.entityManager.refresh(this, lockMode);
    }

    public void Jpa2ActiveRecord_internal.refresh(LockModeType lockMode,
        Map<String, Object> properties) {
        assertEntityManager();
        this.entityManager.refresh(this, lockMode, properties);
    }

    declare @type : @Jpa2ActiveRecordMixin * : @Configurable;

    declare parents : @Jpa2ActiveRecordMixin * implements Jpa2ActiveRecord_internal;
}
