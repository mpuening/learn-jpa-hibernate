package io.github.learnjpahibernate.activerecord;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * All we need to do is to enable spring configured
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableSpringConfigured
public @interface EnableActiveRecordSupport {

}
