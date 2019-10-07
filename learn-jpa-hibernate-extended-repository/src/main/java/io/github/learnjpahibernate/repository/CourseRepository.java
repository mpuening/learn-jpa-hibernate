package io.github.learnjpahibernate.repository;

import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Course;

@Repository
public interface CourseRepository extends ExtendedJpaRepository<Course, Long> {

}
