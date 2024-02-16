package io.github.learnjpahibernate.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.github.learnjpahibernate.data.EntityValidator;
import io.github.learnjpahibernate.model.HailingFrequency.HailingFrequencyId;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

public class PassengerTests extends AbstractEntityTest {

	@Test
	public void validatePassengerClassForExistingEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(passengerRepository);

		Long id = 2L;
		EntityValidator.with(id, passengerRepository).beforeUpdate(p -> {
			p.setName("Aunt Bea");
		}).beforeDelete(p -> {
			// prevent 1+N on cascade delete
			passengerRepository.deleteAssociatedHailingFrequencies(id);
			passengerRepository.deleteAssociatedReservations(id);
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(4));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		// Note the inner joins that follow the one to one relationships
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.id,p1_0.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_0.name "
						+ "from person p1_0 join address a1_0 on a1_0.id=p1_0.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "where p1_0.type='PASSENGER' and p1_0.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		// Here is the update to passenger.
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("update person set home_address_id=?,name=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Aunt Bea")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(3, Matchers.is(id)));
		// Cascade deletes
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from hailing_frequency where person_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("DELETE FROM Reservation_Person rp WHERE rp.person_id = ?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// If orphanRemoval were set to true, there would be a query here to find
		// children
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from person where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
	}

	@Test
	public void validatePassengerClassForRelationshipUpdate() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(passengerRepository);

		Long id = 2L;
		EntityValidator.with(id, passengerRepository).beforeUpdate(p -> {
			// Test updating address and adding a contact
			p.getAddress().setCity("Mayberry, North Carolina");
			HailingFrequency newHailingFrequency = new HailingFrequency(
					new HailingFrequency.HailingFrequencyId(p.getId(), "Hey!"), p);
			p.getHailingFrequencies().add(newHailingFrequency);
		}).skipDelete(true).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(5));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.id,p1_0.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_0.name "
						+ "from person p1_0 "
						+ "join address a1_0 on a1_0.id=p1_0.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "where p1_0.type='PASSENGER' and p1_0.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// Fetching existing list of hailing frequencies
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select hf1_0.person_id,hf1_0.frequency "
						+ "from hailing_frequency hf1_0 "
						+ "where hf1_0.person_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// Merge test of new one
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("select hf1_0.frequency,hf1_0.person_id "
						+ "from hailing_frequency hf1_0 "
						+ "where (hf1_0.frequency,hf1_0.person_id) in ((?,?))")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(id)));
		statementIndex++;
		// insert statements
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into hailing_frequency (frequency,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update address set city=?,planet_id=?,street=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Mayberry, North Carolina")));
	}

	@Test
	public void validatePassengerClassForRelationshipCreation() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(passengerRepository);

		Long id = 2L;
		EntityValidator.with(id, passengerRepository).beforeUpdate(bea -> {
			// Book a new trip for opie with aunt bea
			Address opiesAddress = new Address(null, bea.getAddress().getStreet(), bea.getAddress().getCity(),
					bea.getAddress().getPlanet());
			Passenger opie = new Passenger(null, "Opie", opiesAddress, null, null);
			bea.getReservations().stream().forEach(reservation -> {
				// Iterating here is inefficient
				reservation.getPassengers().add(opie);
			});
		}).skipDelete(true).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(12));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.id,p1_0.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_0.name "
						+ "from person p1_0 "
						+ "join address a1_0 on a1_0.id=p1_0.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "where p1_0.type='PASSENGER' and p1_0.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// Get all Aunt Bea's reservations
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select r1_0.person_id,r1_1.id,r1_1.voyage_id,v1_0.id,v1_0.departure_date,v1_0.departure_planet_id,dp1_0.id,dp1_0.name,v1_0.destination_date,v1_0.destination_planet_id,dp2_0.id,dp2_0.name,v1_0.ship_id,s1_0.id,c1_0.id,c1_0.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p1_0.id,p1_0.name,a1_0.street,c1_0.name,s1_0.name,s1_0.ship_class "
						+ "from reservation_person r1_0 "
						+ "join reservation r1_1 on r1_1.id=r1_0.reservation_id "
						+ "left join voyage v1_0 on v1_0.id=r1_1.voyage_id "
						+ "left join planet dp1_0 on dp1_0.id=v1_0.departure_planet_id "
						+ "left join planet dp2_0 on dp2_0.id=v1_0.destination_planet_id "
						+ "left join ship s1_0 on s1_0.id=v1_0.ship_id "
						+ "left join (select * from person t where t.type='CAPTAIN') c1_0 on s1_0.id=c1_0.ship_id "
						+ "left join address a1_0 on a1_0.id=c1_0.home_address_id "
						+ "left join planet p1_0 on p1_0.id=a1_0.planet_id "
						+ "where r1_0.person_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// For each reservation, collect it's passengers.. see resolve N+1 project
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.reservation_id,p1_1.id,p1_1.type,p1_1.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_1.name,s1_0.id,s1_0.name,s1_0.ship_class "
						+ "from reservation_person p1_0 "
						+ "join person p1_1 on p1_1.id=p1_0.person_id "
						+ "left join address a1_0 on a1_0.id=p1_1.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "left join ship s1_0 on s1_0.id=p1_1.ship_id "
						+ "where p1_0.reservation_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(Matchers.in(Arrays.asList(1L, 2L, 3L, 4L)))));
		statementIndex++;
		// Here is the second one...
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.reservation_id,p1_1.id,p1_1.type,p1_1.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_1.name,s1_0.id,s1_0.name,s1_0.ship_class "
						+ "from reservation_person p1_0 "
						+ "join person p1_1 on p1_1.id=p1_0.person_id "
						+ "left join address a1_0 on a1_0.id=p1_1.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "left join ship s1_0 on s1_0.id=p1_1.ship_id "
						+ "where p1_0.reservation_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(Matchers.in(Arrays.asList(1L, 2L, 3L, 4L)))));
		statementIndex++;
		// Here is the third one...
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.reservation_id,p1_1.id,p1_1.type,p1_1.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_1.name,s1_0.id,s1_0.name,s1_0.ship_class "
						+ "from reservation_person p1_0 "
						+ "join person p1_1 on p1_1.id=p1_0.person_id "
						+ "left join address a1_0 on a1_0.id=p1_1.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "left join ship s1_0 on s1_0.id=p1_1.ship_id "
						+ "where p1_0.reservation_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(Matchers.in(Arrays.asList(1L, 2L, 3L, 4L)))));
		statementIndex++;
		// Here is the fourth one...
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.reservation_id,p1_1.id,p1_1.type,p1_1.home_address_id,a1_0.id,a1_0.city,a1_0.planet_id,p2_0.id,p2_0.name,a1_0.street,p1_1.name,s1_0.id,s1_0.name,s1_0.ship_class "
						+ "from reservation_person p1_0 "
						+ "join person p1_1 on p1_1.id=p1_0.person_id "
						+ "left join address a1_0 on a1_0.id=p1_1.home_address_id "
						+ "left join planet p2_0 on p2_0.id=a1_0.planet_id "
						+ "left join ship s1_0 on s1_0.id=p1_1.ship_id "
						+ "where p1_0.reservation_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(Matchers.in(Arrays.asList(1L, 2L, 3L, 4L)))));
		statementIndex++;
		// Finally, insert Opie's data into the data base
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into address (city,planet_id,street,id) values (?,?,?,default)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("Maple Road")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("insert into person (home_address_id,name,type,id) values (?,?,'PASSENGER',default)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Opie")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
	}

	@Test
	public void validatePassengerClassForNewEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(planetRepository);
		assertNotNull(passengerRepository);

		Planet earth = planetRepository.findByName("Earth");
		Address address = new Address(null, "Elm Street", "Mayberry", earth);

		Passenger andy = new Passenger(null, "Andy Taylor", address, null, null);
		HailingFrequency frequency1 = new HailingFrequency(new HailingFrequencyId(null, "Sheriff"), andy);
		HailingFrequency frequency2 = new HailingFrequency(new HailingFrequencyId(null, "Andy"), andy);
		Set<HailingFrequency> frequencies = new HashSet<>();
		frequencies.add(frequency1);
		frequencies.add(frequency2);
		andy.setHailingFrequencies(frequencies);

		EntityValidator.with(andy, passengerRepository).beforeUpdate(a -> {
			a.setName("Andrew Jackson Taylor");
		}).beforeDelete(a -> {
			// prevent 1+N on cascade delete
			passengerRepository.deleteAssociatedHailingFrequencies(a.getId());
			passengerRepository.deleteAssociatedReservations(a.getId());
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(10));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(4));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(4));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("select p1_0.id,p1_0.name from planet p1_0 where p1_0.name=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Earth")));
		statementIndex++;
		// Cascade inserts
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into address (city,planet_id,street,id) values (?,?,?,default)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("Elm Street")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("insert into person (home_address_id,name,type,id) values (?,?,'PASSENGER',default)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Andy Taylor")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into hailing_frequency (frequency,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(andy.getId())));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into hailing_frequency (frequency,person_id) values (?,?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(andy.getId())));
		// Here is the update to passenger.
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("update person set home_address_id=?,name=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Andrew Jackson Taylor")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(3, Matchers.is(andy.getId())));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from hailing_frequency where person_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(andy.getId())));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("DELETE FROM Reservation_Person rp WHERE rp.person_id = ?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(andy.getId())));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from person where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(andy.getId())));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
	}
}
