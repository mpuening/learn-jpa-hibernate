package io.github.learnjpahibernate.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.github.learnjpahibernate.data.EntityValidator;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

public class VoyageTests extends AbstractEntityTest {

	@Test
	public void validateVoyageClassForExistingEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(voyageRepository);

		Long id = 1L;
		LocalDate newDate = LocalDate.now();
		EntityValidator.with(id, voyageRepository).beforeUpdate(v -> {
			v.setDestinationDate(newDate);
		}).skipDelete(true).withTransactions(transactionTemplate).assertEntityIsValid();

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(2));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select voyage0_.id as id1_9_0_, voyage0_.departure_date as departur2_9_0_, voyage0_.departure_planet_id as departur4_9_0_, voyage0_.destination_date as destinat3_9_0_, voyage0_.destination_planet_id as destinat5_9_0_, voyage0_.ship_id as ship_id6_9_0_, "
						+ "planet1_.id as id1_4_1_, planet1_.name as name2_4_1_, "
						+ "planet2_.id as id1_4_2_, planet2_.name as name2_4_2_, "
						+ "ship3_.id as id1_8_3_, ship3_.name as name2_8_3_, ship3_.ship_class as ship_cla3_8_3_, "
						+ "captain4_.id as id2_3_4_, captain4_.home_address_id as home_add4_3_4_, captain4_.name as name3_3_4_, captain4_.ship_id as ship_id5_3_4_, "
						+ "address5_.id as id1_0_5_, address5_.city as city2_0_5_, address5_.planet_id as planet_i4_0_5_, address5_.street as street3_0_5_, "
						+ "planet6_.id as id1_4_6_, planet6_.name as name2_4_6_ "
						+ "from voyage voyage0_ inner join planet planet1_ on voyage0_.departure_planet_id=planet1_.id "
						+ "inner join planet planet2_ on voyage0_.destination_planet_id=planet2_.id "
						+ "inner join ship ship3_ on voyage0_.ship_id=ship3_.id "
						+ "left outer join person captain4_ on ship3_.id=captain4_.ship_id and captain4_.type='CAPTAIN' "
						+ "left outer join address address5_ on captain4_.home_address_id=address5_.id "
						+ "left outer join planet planet6_ on address5_.planet_id=planet6_.id "
						+ "where voyage0_.id=?")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"update voyage set departure_date=?, departure_planet_id=?, destination_date=?, destination_planet_id=?, ship_id=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.paramAsDate(3,
				Matchers.is(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))));
		// Referential integrity prevents delete, but let's not delete the history of
		// the trips anyways
//		statementIndex++;
//		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
//		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
//				DataSourceAssertMatchers.query(Matchers.is("delete from voyage where id=?")));
//		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
//				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
	}

	@Test
	public void validateVoyageClassForNewEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(planetRepository);
		assertNotNull(shipRepository);
		assertNotNull(voyageRepository);

		LocalDate departureDate = LocalDate.now();
		Planet earth = planetRepository.findByName("Earth");
		LocalDate destinationDate = LocalDate.now();
		Planet vulcan = planetRepository.findByName("Vulcan");
		Ship ship = shipRepository.findById(1L).orElse(null);
		Voyage quickTrip = new Voyage(null, departureDate, destinationDate, earth, vulcan, ship, null);
		proxyDataSource.reset();

		EntityValidator.with(quickTrip, voyageRepository).beforeUpdate(v -> {
			v.setDestinationDate(destinationDate.plusDays(1L));
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"insert into voyage (id, departure_date, departure_planet_id, destination_date, destination_planet_id, ship_id) values (null, ?, ?, ?, ?, ?)")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"update voyage set departure_date=?, departure_planet_id=?, destination_date=?, destination_planet_id=?, ship_id=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.paramAsDate(3,
				Matchers.is(Date.from(destinationDate.plusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant()))));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from voyage where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(quickTrip.getId())));
	}
}
