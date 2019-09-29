package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Planet;

@RepositoryRestResource(collectionResourceRel = "planets", path = "planets")
public interface PlanetRepository extends JpaRepository<Planet, Long> {

	Planet findByName(@Param("name") String name);

}
