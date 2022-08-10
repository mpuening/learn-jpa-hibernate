package io.github.learnjpahibernate.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.github.learnjpahibernate.data.EntityValidator;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

public class AddressTests extends AbstractEntityTest {

	@Test
	public void validateAddressClassForExistingEntity() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(addressRepository);

		Long id = 3L;
		EntityValidator.with(id, addressRepository).beforeUpdate(p -> {
			p.setCity("Washington");
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select a1_0.id,a1_0.city,p1_0.id,p1_0.name,a1_0.street "
						+ "from address a1_0 "
						+ "join planet p1_0 on p1_0.id=a1_0.planet_id "
						+ "where a1_0.id=?")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update address set city=?, planet_id=?, street=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Washington")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(id)));
	}

	@Test
	public void validateAddressClassForNewEntity() {
		assertNotNull(planetRepository);
		assertNotNull(addressRepository);
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		Planet earth = planetRepository.findByName("Earth");
		Address address = new Address(null, "Broadway", "New York", earth);

		EntityValidator.with(address, addressRepository).beforeUpdate(a -> {
			a.setStreet("Broadway Ave");
		}).withTransactions(transactionTemplate).assertEntityIsValid();

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(4));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select p1_0.id,p1_0.name from planet p1_0 where p1_0.name=?")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into address (id, city, planet_id, street) values (default, ?, ?, ?)")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update address set city=?, planet_id=?, street=? where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("New York")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(earth.getId())));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("Broadway Ave")));
		statementIndex++;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
	}
}
