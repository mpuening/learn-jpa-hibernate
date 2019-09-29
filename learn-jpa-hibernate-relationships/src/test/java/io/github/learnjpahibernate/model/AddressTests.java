package io.github.learnjpahibernate.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select address0_.id as id1_0_0_, address0_.city as city2_0_0_, address0_.planet_id as planet_i4_0_0_, address0_.street as street3_0_0_, planet1_.id as id1_4_1_, planet1_.name as name2_4_1_ "
						+ "from address address0_ inner join planet planet1_ on address0_.planet_id=planet1_.id "
						+ "where address0_.id=?")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update address set city=?, planet_id=?, street=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Washington")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(4));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers.is(
				"select planet0_.id as id1_4_, planet0_.name as name2_4_ from planet planet0_ where planet0_.name=?")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("insert into address (id, city, planet_id, street) values (null, ?, ?, ?)")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update address set city=?, planet_id=?, street=? where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("New York")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(earth.getId())));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(3, Matchers.is("Broadway Ave")));
		statementIndex++;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from address where id=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(address.getId())));
	}
}
