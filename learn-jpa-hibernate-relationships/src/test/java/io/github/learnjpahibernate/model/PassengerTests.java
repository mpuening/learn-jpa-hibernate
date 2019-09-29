package io.github.learnjpahibernate.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(4));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		// Note the inner joins that follow the one to one relationships
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select passenger0_.id as id2_3_0_, passenger0_.home_address_id as home_add4_3_0_, passenger0_.name as name3_3_0_, "
						+ "address1_.id as id1_0_1_, address1_.city as city2_0_1_, address1_.planet_id as planet_i4_0_1_, address1_.street as street3_0_1_, "
						+ "planet2_.id as id1_4_2_, planet2_.name as name2_4_2_ "
						+ "from person passenger0_ inner join address address1_ on passenger0_.home_address_id=address1_.id "
						+ "inner join planet planet2_ on address1_.planet_id=planet2_.id "
						+ "where passenger0_.id=? and passenger0_.type='PASSENGER'")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		// Here is the update to passenger.
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("update person set home_address_id=?, name=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Aunt Bea")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(3, Matchers.is(id)));
		// Cascade deletes
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from hailing_frequency where person_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("DELETE FROM Reservation_Person rp WHERE rp.person_id = ?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// If orphanRemoval were set to true, there would be a query here to find
		// children
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from person where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(5));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(3));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select passenger0_.id as id2_3_0_, passenger0_.home_address_id as home_add4_3_0_, passenger0_.name as name3_3_0_, "
						+ "address1_.id as id1_0_1_, address1_.city as city2_0_1_, address1_.planet_id as planet_i4_0_1_, address1_.street as street3_0_1_, "
						+ "planet2_.id as id1_4_2_, planet2_.name as name2_4_2_ "
						+ "from person passenger0_ inner join address address1_ on passenger0_.home_address_id=address1_.id "
						+ "inner join planet planet2_ on address1_.planet_id=planet2_.id "
						+ "where passenger0_.id=? and passenger0_.type='PASSENGER'")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// Fetching existing list of hailing frequencies
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select hailingfre0_.person_id as person_i2_2_0_, hailingfre0_.frequency as frequenc1_2_0_, hailingfre0_.frequency as frequenc1_2_1_, hailingfre0_.person_id as person_i2_2_1_ "
						+ "from hailing_frequency hailingfre0_ where hailingfre0_.person_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// Merge test of new one
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("select hailingfre0_.frequency as frequenc1_2_0_, hailingfre0_.person_id as person_i2_2_0_ "
						+ "from hailing_frequency hailingfre0_ where hailingfre0_.frequency=? and hailingfre0_.person_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(id)));
		statementIndex++;
		// insert statements
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into hailing_frequency (frequency, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(id)));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update address set city=?, planet_id=?, street=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(12));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select passenger0_.id as id2_3_0_, passenger0_.home_address_id as home_add4_3_0_, passenger0_.name as name3_3_0_, "
						+ "address1_.id as id1_0_1_, address1_.city as city2_0_1_, address1_.planet_id as planet_i4_0_1_, address1_.street as street3_0_1_, "
						+ "planet2_.id as id1_4_2_, planet2_.name as name2_4_2_ "
						+ "from person passenger0_ inner join address address1_ on passenger0_.home_address_id=address1_.id "
						+ "inner join planet planet2_ on address1_.planet_id=planet2_.id "
						+ "where passenger0_.id=? and passenger0_.type='PASSENGER'")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// Get all Aunt Bea's reservations
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select reservatio0_.person_id as person_i2_7_0_, reservatio0_.reservation_id as reservat1_7_0_, "
						+ "reservatio1_.id as id1_5_1_, reservatio1_.voyage_id as voyage_i2_5_1_, "
						+ "voyage2_.id as id1_9_2_, voyage2_.departure_date as departur2_9_2_, voyage2_.departure_planet_id as departur4_9_2_, voyage2_.destination_date as destinat3_9_2_, voyage2_.destination_planet_id as destinat5_9_2_, voyage2_.ship_id as ship_id6_9_2_, planet3_.id as id1_4_3_, "
						+ "planet3_.name as name2_4_3_, planet4_.id as id1_4_4_, "
						+ "planet4_.name as name2_4_4_, ship5_.id as id1_8_5_, "
						+ "ship5_.name as name2_8_5_, ship5_.ship_class as ship_cla3_8_5_, "
						+ "captain6_.id as id2_3_6_, captain6_.home_address_id as home_add4_3_6_, captain6_.name as name3_3_6_, captain6_.ship_id as ship_id5_3_6_, "
						+ "address7_.id as id1_0_7_, address7_.city as city2_0_7_, address7_.planet_id as planet_i4_0_7_, address7_.street as street3_0_7_, "
						+ "planet8_.id as id1_4_8_, planet8_.name as name2_4_8_ "
						+ "from reservation_person reservatio0_ inner join reservation reservatio1_ on reservatio0_.reservation_id=reservatio1_.id "
						+ "inner join voyage voyage2_ on reservatio1_.voyage_id=voyage2_.id "
						+ "inner join planet planet3_ on voyage2_.departure_planet_id=planet3_.id "
						+ "inner join planet planet4_ on voyage2_.destination_planet_id=planet4_.id "
						+ "inner join ship ship5_ on voyage2_.ship_id=ship5_.id "
						+ "left outer join person captain6_ on ship5_.id=captain6_.ship_id and captain6_.type='CAPTAIN' "
						+ "left outer join address address7_ on captain6_.home_address_id=address7_.id "
						+ "left outer join planet planet8_ on address7_.planet_id=planet8_.id "
						+ "where reservatio0_.person_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// For each reservation, collect it's passengers.. see resolve N+1 project
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select passengers0_.reservation_id as reservat1_7_0_, passengers0_.person_id as person_i2_7_0_, "
						+ "person1_.id as id2_3_1_, person1_.home_address_id as home_add4_3_1_, person1_.name as name3_3_1_, person1_.ship_id as ship_id5_3_1_, person1_.type as type1_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_, "
						+ "ship4_.id as id1_8_4_, ship4_.name as name2_8_4_, ship4_.ship_class as ship_cla3_8_4_ "
						+ "from reservation_person passengers0_ inner join person person1_ on passengers0_.person_id=person1_.id left outer join address address2_ on person1_.home_address_id=address2_.id left outer join planet planet3_ on address2_.planet_id=planet3_.id left outer join ship ship4_ on person1_.ship_id=ship4_.id "
						+ "where passengers0_.reservation_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.isIn(Arrays.asList(1L, 2L, 3L, 4L))));
		statementIndex++;
		// Here is the second one...
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select passengers0_.reservation_id as reservat1_7_0_, passengers0_.person_id as person_i2_7_0_, "
						+ "person1_.id as id2_3_1_, person1_.home_address_id as home_add4_3_1_, person1_.name as name3_3_1_, person1_.ship_id as ship_id5_3_1_, person1_.type as type1_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_, "
						+ "ship4_.id as id1_8_4_, ship4_.name as name2_8_4_, ship4_.ship_class as ship_cla3_8_4_ "
						+ "from reservation_person passengers0_ inner join person person1_ on passengers0_.person_id=person1_.id left outer join address address2_ on person1_.home_address_id=address2_.id left outer join planet planet3_ on address2_.planet_id=planet3_.id left outer join ship ship4_ on person1_.ship_id=ship4_.id "
						+ "where passengers0_.reservation_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.isIn(Arrays.asList(1L, 2L, 3L, 4L))));
		statementIndex++;
		// Here is the third one...
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select passengers0_.reservation_id as reservat1_7_0_, passengers0_.person_id as person_i2_7_0_, "
						+ "person1_.id as id2_3_1_, person1_.home_address_id as home_add4_3_1_, person1_.name as name3_3_1_, person1_.ship_id as ship_id5_3_1_, person1_.type as type1_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_, "
						+ "ship4_.id as id1_8_4_, ship4_.name as name2_8_4_, ship4_.ship_class as ship_cla3_8_4_ "
						+ "from reservation_person passengers0_ inner join person person1_ on passengers0_.person_id=person1_.id left outer join address address2_ on person1_.home_address_id=address2_.id left outer join planet planet3_ on address2_.planet_id=planet3_.id left outer join ship ship4_ on person1_.ship_id=ship4_.id "
						+ "where passengers0_.reservation_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.isIn(Arrays.asList(1L, 2L, 3L, 4L))));
		statementIndex++;
		// Here is the fourth one...
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select passengers0_.reservation_id as reservat1_7_0_, passengers0_.person_id as person_i2_7_0_, "
						+ "person1_.id as id2_3_1_, person1_.home_address_id as home_add4_3_1_, person1_.name as name3_3_1_, person1_.ship_id as ship_id5_3_1_, person1_.type as type1_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_, "
						+ "ship4_.id as id1_8_4_, ship4_.name as name2_8_4_, ship4_.ship_class as ship_cla3_8_4_ "
						+ "from reservation_person passengers0_ inner join person person1_ on passengers0_.person_id=person1_.id left outer join address address2_ on person1_.home_address_id=address2_.id left outer join planet planet3_ on address2_.planet_id=planet3_.id left outer join ship ship4_ on person1_.ship_id=ship4_.id "
						+ "where passengers0_.reservation_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.isIn(Arrays.asList(1L, 2L, 3L, 4L))));
		statementIndex++;
		// Finally, insert Opie's data into the data base
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into address (id, city, planet_id, street) values (null, ?, ?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("Maple Road")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("insert into person (id, home_address_id, name, type) values (null, ?, ?, 'PASSENGER')")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Opie")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.greaterThan(2L)));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into reservation_person (reservation_id, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(10));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(4));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(4));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("select planet0_.id as id1_4_, planet0_.name as name2_4_ "
						+ "from planet planet0_ where planet0_.name=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Earth")));
		statementIndex++;
		// Cascade inserts
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into address (id, city, planet_id, street) values (null, ?, ?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("Elm Street")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("insert into person (id, home_address_id, name, type) values (null, ?, ?, 'PASSENGER')")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Andy Taylor")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into hailing_frequency (frequency, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(andy.getId())));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into hailing_frequency (frequency, person_id) values (?, ?)")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(andy.getId())));
		// Here is the update to passenger.
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("update person set home_address_id=?, name=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Andrew Jackson Taylor")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(3, Matchers.is(andy.getId())));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from hailing_frequency where person_id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(andy.getId())));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("DELETE FROM Reservation_Person rp WHERE rp.person_id = ?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(andy.getId())));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from person where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(andy.getId())));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
	}
}
