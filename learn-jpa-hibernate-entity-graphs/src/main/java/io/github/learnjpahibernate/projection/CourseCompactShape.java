package io.github.learnjpahibernate.projection;

import org.springframework.data.rest.core.config.Projection;

import io.github.learnjpahibernate.model.Course;

@Projection(name = "compact", types = { Course.class })
public interface CourseCompactShape {

	Long getId();

	String getName();
}
