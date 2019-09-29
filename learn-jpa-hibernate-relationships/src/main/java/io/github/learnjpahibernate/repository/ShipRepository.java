package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Ship;

@RepositoryRestResource(collectionResourceRel = "ships", path = "ships")
public interface ShipRepository extends JpaRepository<Ship, Long> {

	/**
	 * Prevent the 1+N problem from cascade delete
	 * 
	 * @param id
	 */
	@Modifying
	@Query("DELETE FROM Cabin c WHERE c.ship.id = ?1")
	void deleteAssociatedCabins(Long shipId);

	/**
	 * But support deleting one cabin
	 * 
	 * @param id
	 */
	@Modifying
	@Query("DELETE FROM Cabin c WHERE c.id = ?1")
	void deleteCabin(Long cabinId);
}
