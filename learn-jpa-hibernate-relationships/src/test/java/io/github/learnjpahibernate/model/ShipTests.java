package io.github.learnjpahibernate.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

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
				.is("select ship0_.id as id1_8_0_, ship0_.name as name2_8_0_, ship0_.ship_class as ship_cla3_8_0_, "
						+ "captain1_.id as id2_3_1_, captain1_.home_address_id as home_add4_3_1_, captain1_.name as name3_3_1_, captain1_.ship_id as ship_id5_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_ "
						+ "from ship ship0_ left outer join person captain1_ on ship0_.id=captain1_.ship_id and captain1_.type='CAPTAIN' "
						+ "left outer join address address2_ on captain1_.home_address_id=address2_.id "
						+ "left outer join planet planet3_ on address2_.planet_id=planet3_.id "
						+ "where ship0_.id=?")));
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
				.is("select ship0_.id as id1_8_0_, ship0_.name as name2_8_0_, ship0_.ship_class as ship_cla3_8_0_, "
						+ "captain1_.id as id2_3_1_, captain1_.home_address_id as home_add4_3_1_, captain1_.name as name3_3_1_, captain1_.ship_id as ship_id5_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_ "
						+ "from ship ship0_ left outer join person captain1_ on ship0_.id=captain1_.ship_id and captain1_.type='CAPTAIN' "
						+ "left outer join address address2_ on captain1_.home_address_id=address2_.id "
						+ "left outer join planet planet3_ on address2_.planet_id=planet3_.id "
						+ "where ship0_.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select cabins0_.ship_id as ship_id6_1_0_, cabins0_.id as id1_1_0_, cabins0_.id as id1_1_1_, cabins0_.bed_count as bed_coun2_1_1_, cabins0_.deck_level as deck_lev3_1_1_, cabins0_.price as price4_1_1_, cabins0_.currency as currency5_1_1_, cabins0_.ship_id as ship_id6_1_1_ "
						+ "from cabin cabins0_ where cabins0_.ship_id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("update cabin set bed_count=?, deck_level=?, price=?, currency=?, ship_id=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(5, Matchers.is(id)));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(4, Matchers.is("USD")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsBigDecimal(3, Matchers.is(BigDecimal.valueOf(10999L))));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(
				Matchers.is("update cabin set bed_count=?, deck_level=?, price=?, currency=?, ship_id=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(5, Matchers.is(id)));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(4, Matchers.is("USD")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsBigDecimal(3, Matchers.is(BigDecimal.valueOf(10999L))));
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
				.is("select ship0_.id as id1_8_0_, ship0_.name as name2_8_0_, ship0_.ship_class as ship_cla3_8_0_, "
						+ "captain1_.id as id2_3_1_, captain1_.home_address_id as home_add4_3_1_, captain1_.name as name3_3_1_, captain1_.ship_id as ship_id5_3_1_, "
						+ "address2_.id as id1_0_2_, address2_.city as city2_0_2_, address2_.planet_id as planet_i4_0_2_, address2_.street as street3_0_2_, "
						+ "planet3_.id as id1_4_3_, planet3_.name as name2_4_3_ "
						+ "from ship ship0_ left outer join person captain1_ on ship0_.id=captain1_.ship_id and captain1_.type='CAPTAIN' "
						+ "left outer join address address2_ on captain1_.home_address_id=address2_.id "
						+ "left outer join planet planet3_ on address2_.planet_id=planet3_.id "
						+ "where ship0_.id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select cabins0_.ship_id as ship_id6_1_0_, cabins0_.id as id1_1_0_, cabins0_.id as id1_1_1_, cabins0_.bed_count as bed_coun2_1_1_, cabins0_.deck_level as deck_lev3_1_1_, cabins0_.price as price4_1_1_, cabins0_.currency as currency5_1_1_, cabins0_.ship_id as ship_id6_1_1_ "
						+ "from cabin cabins0_ where cabins0_.ship_id=?")));
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
				.query(Matchers.is("insert into ship (id, name, ship_class) values (null, ?, ?)")));
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
