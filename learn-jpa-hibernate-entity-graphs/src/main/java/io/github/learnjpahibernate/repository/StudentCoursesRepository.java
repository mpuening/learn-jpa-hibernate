package io.github.learnjpahibernate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Student;

@Repository
public interface StudentCoursesRepository extends JpaRepository<Student, Long> {

	@EntityGraph(value = "student-courses-entity-graph")
    List<Student> findAll();
	
	@EntityGraph("student-courses-entity-graph")
    List<Student> findByCoursesName(String name);
}
