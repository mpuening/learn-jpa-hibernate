package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Student;

@RepositoryRestResource(collectionResourceRel = "students", path = "students")
public interface StudentRepository extends JpaRepository<Student, Long> {

}
