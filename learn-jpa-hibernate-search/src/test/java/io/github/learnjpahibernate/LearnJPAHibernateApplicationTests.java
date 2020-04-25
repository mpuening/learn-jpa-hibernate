package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.model.Event;
import io.github.learnjpahibernate.service.EventService;
import net.ttddyy.dsproxy.asserts.PreparedExecution;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected EventService eventService;

	@Test
	@Transactional
	public void testEventSearch() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		List<Event> events = eventService.searchEvents("birthday");
		assertNotNull(events);
		assertEquals(2, events.size());
		assertEquals("Boy Birthday Party", events.get(0).getDescription());
		assertEquals("Girl Birthday Party", events.get(1).getDescription());

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.otherCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select this_.id as id1_0_0_, this_.created_by as created_2_0_0_, this_.created_date as created_3_0_0_, this_.description as descript4_0_0_, this_.last_modified_by as last_mod5_0_0_, this_.last_modified_date as last_mod6_0_0_ "
						+ "from event this_ where (this_.id in (?, ?))")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.is(5L)));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.is(15L)));
	}

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
