package io.github.learnjpahibernate.activerecord.aspect;

import org.springframework.beans.factory.annotation.Configurable;

public aspect Jpa1ActiveRecordAspect extends AbstractJpa1ActiveRecordAspect
{
    public interface Jpa1ActiveRecord_internal extends AbstractJpa1ActiveRecord_internal {
    }

    declare parents : @Jpa1ActiveRecordMixin * implements Jpa1ActiveRecord_internal;

    declare @type : @Jpa1ActiveRecordMixin * : @Configurable;
}
