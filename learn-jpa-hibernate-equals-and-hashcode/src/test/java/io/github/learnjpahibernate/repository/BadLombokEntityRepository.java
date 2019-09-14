package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.BadLombokEntity;

@Repository
public interface BadLombokEntityRepository extends JpaRepository<BadLombokEntity, String> {

}
