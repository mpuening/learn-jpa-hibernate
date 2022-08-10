package io.github.learnjpahibernate.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

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

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(2));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select v1_0.id,v1_0.departure_date,d1_0.id,d1_0.name,v1_0.destination_date,d2_0.id,d2_0.name,s1_0.id,c1_0.id,a1_0.id,a1_0.city,p1_0.id,p1_0.name,a1_0.street,c1_0.name,s1_0.name,s1_0.ship_class "
						+ "from voyage v1_0 "
						+ "join planet d1_0 on d1_0.id=v1_0.departure_planet_id "
						+ "join planet d2_0 on d2_0.id=v1_0.destination_planet_id "
						+ "join ship s1_0 on s1_0.id=v1_0.ship_id "
						+ "left join person c1_0 on s1_0.id=c1_0.ship_id "
						+ "left join address a1_0 on a1_0.id=c1_0.home_address_id "
						+ "left join planet p1_0 on p1_0.id=a1_0.planet_id "
						+ "where v1_0.id=?")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("update voyage set departure_date=?, departure_planet_id=?, destination_date=?, destination_planet_id=?, ship_id=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.paramAsDate(3,
				Matchers.is(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))));
		// Referential integrity prevents delete, but let's not delete the history of
		// the trips anyways
//		statementIndex++;
//		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
//		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
//				DataSourceAssertMatchers.query(Matchers.is("delete from voyage where id=?")));
//		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("insert into voyage (id, departure_date, departure_planet_id, destination_date, destination_planet_id, ship_id) values (default, ?, ?, ?, ?, ?)")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("update voyage set departure_date=?, departure_planet_id=?, destination_date=?, destination_planet_id=?, ship_id=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.paramAsDate(3,
				Matchers.is(Date.from(destinationDate.plusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant()))));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from voyage where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(quickTrip.getId())));
	}
}
