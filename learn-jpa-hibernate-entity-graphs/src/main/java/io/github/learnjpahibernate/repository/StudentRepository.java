package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

}
