package io.github.learnjpahibernate.activerecord.aspect;

import java.util.Map;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

public aspect Jpa2ActiveRecordAspect extends AbstractJpa2ActiveRecordAspect {

    public interface Jpa2ActiveRecord_internal extends AbstractJpa2ActiveRecord_internal {
    }

    declare @type : @Jpa2ActiveRecordMixin * : @Configurable;

    declare parents : @Jpa2ActiveRecordMixin * implements Jpa2ActiveRecord_internal;
}
