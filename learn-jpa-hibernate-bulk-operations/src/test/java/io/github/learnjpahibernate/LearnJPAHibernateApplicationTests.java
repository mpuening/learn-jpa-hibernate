package io.github.learnjpahibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.learnjpahibernate.repository.LateInvoiceRepository;
import io.github.learnjpahibernate.service.InvoiceService;
import net.ttddyy.dsproxy.asserts.PreparedExecution;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@RunWith(SpringRunner.class)
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex), DataSourceAssertMatchers
				.query(Matchers.is("update invoice set account_manager=? where account_manager=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.paramAsString(1, Matchers.is("Alice")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(1));

		int statementIndex = 0;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("delete from invoice where name=?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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

		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(2));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));

		int statementIndex = 1;
		Assert.assertThat(getExecution(proxyDataSource, statementIndex), DataSourceAssertMatchers.isPrepared());
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
				DataSourceAssertMatchers.query(Matchers.is("insert into LATE_INVOICE(ID,NAME,REVIEW_DATE) "
						+ "select invoice0_.id as col_0_0_, invoice0_.name as col_1_0_, '2019-12-31' as col_2_0_ "
						+ "from invoice invoice0_ where invoice0_.name like ?")));
		Assert.assertThat(getPrepared(proxyDataSource, statementIndex),
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
