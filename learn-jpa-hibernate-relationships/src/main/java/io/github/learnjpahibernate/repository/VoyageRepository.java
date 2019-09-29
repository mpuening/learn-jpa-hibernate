package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Planet;
import io.github.learnjpahibernate.model.Voyage;

@RepositoryRestResource(collectionResourceRel = "voyages", path = "voyages")
public interface VoyageRepository extends JpaRepository<Voyage, Long> {

	Voyage findByDeparturePlanetAndDestinationPlanet(Planet departurePlanet, Planet destinationPlanet);

}
