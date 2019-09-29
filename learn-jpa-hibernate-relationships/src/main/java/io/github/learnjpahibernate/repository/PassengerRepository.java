package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.Passenger;

@RepositoryRestResource(collectionResourceRel = "passengers", path = "passengers")
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

	Passenger findByName(@Param("name") String name);

	/**
	 * Prevent the 1+N problem from cascade delete
	 * 
	 * @param id
	 */
	@Modifying
	@Query("DELETE FROM HailingFrequency h WHERE h.id.personId = ?1")
	void deleteAssociatedHailingFrequencies(Long personId);

	/**
	 * Prevent the 1+N problem from cascade delete
	 * 
	 * @param id
	 */
	@Modifying
	@Query(value = "DELETE FROM Reservation_Person rp WHERE rp.person_id = ?1", nativeQuery = true)
	void deleteAssociatedReservations(Long personId);
}
