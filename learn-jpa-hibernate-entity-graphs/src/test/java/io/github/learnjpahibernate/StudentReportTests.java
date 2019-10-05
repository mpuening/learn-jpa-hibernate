package io.github.learnjpahibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.learnjpahibernate.service.StudentReportService;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@RunWith(SpringRunner.class)
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
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(18));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(18));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
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
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
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
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
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
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(8));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(8));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
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
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
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
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}
}
