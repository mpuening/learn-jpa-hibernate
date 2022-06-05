package io.github.learnjpahibernate.repository;

import jakarta.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	@Query("select t from Teacher t where name = ?1")
	@QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }, forCounting = false)
	Teacher findTeacherByName(String name);
}
