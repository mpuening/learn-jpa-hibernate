package io.github.learnjpahibernate.repository;

import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Student;

@Repository
public interface StudentRepository extends ExtendedJpaRepository<Student, Long> {

}
