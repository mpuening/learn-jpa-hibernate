package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.BadSimpleEntity;

@Repository
public interface BadSimpleEntityRepository extends JpaRepository<BadSimpleEntity, Long> {

}
