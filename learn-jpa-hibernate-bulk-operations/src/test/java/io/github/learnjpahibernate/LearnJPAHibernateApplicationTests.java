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

import io.github.learnjpahibernate.repository.LateInvoiceRepository;
import io.github.learnjpahibernate.service.InvoiceService;
import net.ttddyy.dsproxy.asserts.PreparedExecution;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@SpringBootTest
public class LearnJPAHibernateApplicationTests {
	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected LateInvoiceRepository lateInvoiceRepository;

	@Autowired
	protected InvoiceService invoiceService;

	@Test
	public void testReassignInvoices() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(invoiceService);

		int rowsUpdated = invoiceService.reassignInvoices("Jim", "Alice");
		assertEquals(2, rowsUpdated);

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update invoice set account_manager=? where account_manager=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Alice")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(2, Matchers.is("Jim")));
	}

	@Test
	public void testRemoveInvoices() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(invoiceService);

		int rowsUpdated = invoiceService.removeInvoices("Douglas");
		assertEquals(1, rowsUpdated);

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from invoice where name=?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Douglas")));
	}

	@Test
	public void testCopyLateInvoices() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(invoiceService);

		long count = lateInvoiceRepository.count();
		assertEquals(0L, count);

		int rowsCopied = invoiceService.copyLateInvoices("Douglas");
		assertEquals(2, rowsCopied);

		count = lateInvoiceRepository.count();
		assertEquals(2L, count);

		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(2));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 1;
		MatcherAssert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("insert into LATE_INVOICE(ID,NAME,REVIEW_DATE) "
						+ "select invoice0_.id as col_0_0_, invoice0_.name as col_1_0_, '2019-12-31' as col_2_0_ "
						+ "from invoice invoice0_ where invoice0_.name like ?")));
		MatcherAssert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("%Douglas%")));
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
