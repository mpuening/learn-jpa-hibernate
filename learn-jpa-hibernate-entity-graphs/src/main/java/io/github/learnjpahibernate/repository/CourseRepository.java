package io.github.learnjpahibernate.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("FROM Course c")
	Stream<Course> queryAllStreaming();
}
