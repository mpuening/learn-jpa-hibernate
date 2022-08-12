package io.github.learnjpahibernate.activerecord.aspect;

import java.util.Map;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

public aspect Jpa3ActiveRecordAspect extends AbstractJpa3ActiveRecordAspect {

    public interface Jpa3ActiveRecord_internal extends AbstractJpa3ActiveRecord_internal {
    }

    declare @type : @Jpa3ActiveRecordMixin * : @Configurable;

    declare parents : @Jpa3ActiveRecordMixin * implements Jpa3ActiveRecord_internal;
}
