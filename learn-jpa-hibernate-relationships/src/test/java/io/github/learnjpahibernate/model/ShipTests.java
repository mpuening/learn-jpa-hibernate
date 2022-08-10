package io.github.learnjpahibernate.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import io.github.learnjpahibernate.data.EntityValidator;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

public class ShipTests extends AbstractEntityTest {

	@Test
	public void validateShipClassForExistingEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(shipRepository);

		Long id = 2L;
		EntityValidator.with(id, shipRepository).beforeUpdate(s -> {
			s.setName("U.S.S. Billings");
		}).beforeDelete(s -> {
			shipRepository.deleteAssociatedCabins(s.getId());
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(4));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(2));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select s1_0.id,c1_0.id,a1_0.id,a1_0.city,p1_0.id,p1_0.name,a1_0.street,c1_0.name,s1_0.name,s1_0.ship_class "
						+ "from ship s1_0 "
						+ "left join person c1_0 on s1_0.id=c1_0.ship_id "
						+ "left join address a1_0 on a1_0.id=c1_0.home_address_id "
						+ "left join planet p1_0 on p1_0.id=a1_0.planet_id "
						+ "where s1_0.id=?")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("update ship set name=?, ship_class=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("U.S.S. Billings")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from cabin where ship_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		// If orphanRemoval were set to true, there would be a query here to find cabins
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from ship where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
	}

	@Test
	public void validateShipClassForRelationshipUpdate() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(shipRepository);

		Long id = 3L;
		EntityValidator.with(id, shipRepository).beforeUpdate(s -> {
			// Add $10 to the price of each cabin, $99.99 (see test-case-data.sql)
			CurrencyUnit usd = Monetary.getCurrency("USD");
			MonetaryAmount currentPrice = Money.ofMinor(usd, 9999L);
			MonetaryAmount tenBucks = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(10L).create();
			s.getCabins().stream().forEach(cabin -> {
				assertEquals(currentPrice, cabin.getPrice());
				cabin.setPrice(cabin.getPrice().add(tenBucks));
			});
		}).skipDelete(true).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(4));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(2));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(2));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select s1_0.id,c1_0.id,a1_0.id,a1_0.city,p1_0.id,p1_0.name,a1_0.street,c1_0.name,s1_0.name,s1_0.ship_class "
						+ "from ship s1_0 "
						+ "left join person c1_0 on s1_0.id=c1_0.ship_id "
						+ "left join address a1_0 on a1_0.id=c1_0.home_address_id "
						+ "left join planet p1_0 on p1_0.id=a1_0.planet_id "
						+ "where s1_0.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select c1_0.ship_id,c1_0.id,c1_0.bed_count,c1_0.deck_level,c1_0.currency,c1_0.price "
						+ "from cabin c1_0 "
						+ "where c1_0.ship_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("update cabin set bed_count=?, deck_level=?, currency=?, price=?, ship_id=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(5, Matchers.is(id)));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("USD")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(4, Matchers.is(Long.valueOf(10999L))));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("update cabin set bed_count=?, deck_level=?, currency=?, price=?, ship_id=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(5, Matchers.is(id)));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("USD")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(4, Matchers.is(Long.valueOf(10999L))));
	}

	@Test
	public void validateShipClassForRelationshipRemoveCabin() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(shipRepository);

		Long id = 3L;
		EntityValidator.with(id, shipRepository).beforeUpdate(s -> {
			Cabin cabin = s.getCabins().iterator().next();
			shipRepository.deleteCabin(cabin.getId());
			// This does nothing because we elected to not support orphan removal.
			s.getCabins().clear();
		}).skipDelete(true).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(2));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select s1_0.id,c1_0.id,a1_0.id,a1_0.city,p1_0.id,p1_0.name,a1_0.street,c1_0.name,s1_0.name,s1_0.ship_class "
						+ "from ship s1_0 "
						+ "left join person c1_0 on s1_0.id=c1_0.ship_id "
						+ "left join address a1_0 on a1_0.id=c1_0.home_address_id "
						+ "left join planet p1_0 on p1_0.id=a1_0.planet_id "
						+ "where s1_0.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select c1_0.ship_id,c1_0.id,c1_0.bed_count,c1_0.deck_level,c1_0.currency,c1_0.price "
						+ "from cabin c1_0 "
						+ "where c1_0.ship_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from cabin where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.greaterThanOrEqualTo(1L)));
	}

	@Test
	public void validateShipClassForNewEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(captainRepository);
		assertNotNull(shipRepository);
		Captain captain = captainRepository.findByName("James T. Kirk");
		Ship ship = new Ship(null, "Enterprise-A", "Constitution", captain, null);
		proxyDataSource.reset();

		EntityValidator.with(ship, shipRepository).beforeUpdate(s -> {
			s.setName("USS Enterprise-A");
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into ship (id, name, ship_class) values (default, ?, ?)")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("update ship set name=?, ship_class=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("USS Enterprise-A")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from ship where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(ship.getId())));
	}
}
