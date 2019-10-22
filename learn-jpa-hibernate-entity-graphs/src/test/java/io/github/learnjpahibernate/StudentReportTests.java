package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.learnjpahibernate.service.StudentReportService;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@SpringBootTest
public class StudentReportTests {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected StudentReportService studentReportService;

	@Test
	public void testCreatingStudentReportPoorly() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		String report = studentReportService.generateReportPoorly();
		System.out.println(report);
		assertEquals(36, report.split("\n").length);

		// See? Ugly... 18 queries to produce a 36 line report....
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(18));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(18));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testCreatingStudentReportWithJoinQuery() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		String report = studentReportService.generateReportWithJoinQuery();
		assertEquals(36, report.split("\n").length);

		// Nice!... one query to produce a 36 line report....
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testCreatingStudentReportWithCriteriaQuery() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		String report = studentReportService.generateReportWithCriteriaQuery();
		assertEquals(36, report.split("\n").length);

		// Nice!... one query to produce a 36 line report....
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testCreatingStudentReportWithPartialEntityGraph() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		String report = studentReportService.generateReportWithPartialEntityGraph();
		assertEquals(36, report.split("\n").length);

		// Slightly better... 8 queries to produce a 36 line report....
		// Teachers are not part of the graph
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(8));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(8));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testCreatingStudentReportWithFullEntityGraph() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		String report = studentReportService.generateReportWithFullEntityGraph();
		assertEquals(36, report.split("\n").length);

		// Nice!... 1 query to produce a 36 line report....
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}
	
	@Test
	public void testCreatingStudentReportWithDynamicEntityGraph() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		String report = studentReportService.generateReportWithDynamicEntityGraph();
		assertEquals(36, report.split("\n").length);

		// Nice!... 1 query to produce a 36 line report....
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}
}
