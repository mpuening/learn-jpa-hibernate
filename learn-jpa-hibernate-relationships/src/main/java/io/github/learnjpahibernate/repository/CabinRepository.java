package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Cabin;

@RepositoryRestResource(collectionResourceRel = "cabins", path = "cabins")
public interface CabinRepository extends JpaRepository<Cabin, Long> {

}
