package io.github.learnjpahibernate.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Course;

@RepositoryRestResource(collectionResourceRel = "courses", path = "courses")
public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("SELECT c FROM Course c")
	Stream<Course> queryAllStreaming();
}
