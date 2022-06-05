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
		// Need to support results in any order
		assertTrue("Boy Birthday Party".equals(events.get(0).getDescription()) || "Girl Birthday Party".equals(events.get(0).getDescription()));
		assertTrue("Boy Birthday Party".equals(events.get(1).getDescription()) || "Girl Birthday Party".equals(events.get(1).getDescription()));

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.otherCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers.query(Matchers
				.is("select event0_.id as id1_0_, event0_.created_by as created_2_0_, event0_.created_date as created_3_0_, event0_.description as descript4_0_, event0_.last_modified_by as last_mod5_0_, event0_.last_modified_date as last_mod6_0_ "
						+ "from event event0_ where event0_.id in (? , ?)")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(1, Matchers.in(List.of(5L, 15L))));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsLong(2, Matchers.in(List.of(15L, 5L))));
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
