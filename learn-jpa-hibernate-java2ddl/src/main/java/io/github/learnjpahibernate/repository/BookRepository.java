package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

}
