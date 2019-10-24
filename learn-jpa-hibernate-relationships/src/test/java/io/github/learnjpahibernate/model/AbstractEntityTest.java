package io.github.learnjpahibernate.model;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import io.github.learnjpahibernate.config.DatasourceProxyConfiguration;
import io.github.learnjpahibernate.repository.AddressRepository;
import io.github.learnjpahibernate.repository.CaptainRepository;
import io.github.learnjpahibernate.repository.PassengerRepository;
import io.github.learnjpahibernate.repository.PlanetRepository;
import io.github.learnjpahibernate.repository.ShipRepository;
import io.github.learnjpahibernate.repository.VoyageRepository;
import net.ttddyy.dsproxy.asserts.PreparedExecution;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;

@DataJpaTest
@Import(DatasourceProxyConfiguration.class)
@Sql("/test-case-data.sql")
public abstract class AbstractEntityTest {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected TransactionTemplate transactionTemplate;

	@Autowired
	protected PlanetRepository planetRepository;

	@Autowired
	protected AddressRepository addressRepository;

	@Autowired
	protected PassengerRepository passengerRepository;

	@Autowired
	protected CaptainRepository captainRepository;

	@Autowired
	protected ShipRepository shipRepository;

	@Autowired
	protected VoyageRepository voyageRepository;

	protected QueryExecution getExecution(ProxyTestDataSource dataSource, int index) {
		return getExecutionByType(dataSource, index, QueryExecution.class);
	}

	protected PreparedExecution getPrepared(ProxyTestDataSource dataSource, int index) {
		return getExecutionByType(dataSource, index, PreparedExecution.class);
	}

	@SuppressWarnings("unchecked")
	protected <T extends QueryExecution> T getExecutionByType(ProxyTestDataSource dataSource, int index,
			Class<T> type) {
		List<T> filtered = new ArrayList<>();
		for (QueryExecution queryExecution : dataSource.getQueryExecutions()) {
			if (type.isAssignableFrom(queryExecution.getClass())) {
				filtered.add((T) queryExecution);
			}
		}
		return filtered.get(index);
	}
}
