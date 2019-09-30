package io.github.learnjpahibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import io.github.learnjpahibernate.activerecord.Course;
import io.github.learnjpahibernate.activerecord.Student;
import io.github.learnjpahibernate.activerecord.aspect.Jpa2EntityFinder;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected TransactionTemplate transactionTemplate;

	@Test
	public void testUpdateExistingEntities() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		transactionTemplate.execute(status -> {
			// Update the algebra course
			Course algebra = Course.find(Course.class, Long.valueOf(1L));
			assertNotNull(algebra);
			assertEquals(Long.valueOf(1L), algebra.getId());
			assertEquals("Algebra", algebra.getName());
			algebra.setName("Algebra!!");

			// Sign Alice up for algebra
			Student alice = Jpa2EntityFinder.find(Student.class, Long.valueOf(1L));
			assertNotNull(alice);
			assertEquals(Long.valueOf(1L), alice.getId());
			assertEquals("Alice", alice.getName());

			Set<Course> courses = alice.getCourses();
			assertNotNull(courses);
			assertEquals(4, courses.size());
			courses.add(algebra);

			// Jack is dropping out
			Student jack = Jpa2EntityFinder.find(Student.class, Long.valueOf(10L));
			assertNotNull(jack);
			assertEquals(Long.valueOf(10L), jack.getId());
			assertEquals("Jack", jack.getName());
			jack.remove();

			// Flush to make sure statements get executed
			status.flush();
			return null;
		});

		// Four select statements for algebra, alice, her courses, and jack
		// One update statement for algebra
		// One insert statement to sign up alice for algebra
		// Two delete statements for Jack and his courses
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(8));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(4));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(2));
	}

	@Test
	public void testCreateNewEntity() {

		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		transactionTemplate.execute(status -> {

			// New student Pat will sign up for every course
			Student pat = new Student();
			pat.setName("Pat");
			pat.persist();
			Course.createQuery("From Course", Course.class).getResultStream().forEach(course -> {
				pat.getCourses().add(course);
			});

			// Flush to make sure statements get executed
			status.flush();
			return null;
		});

		// 15 courses and one person equals 16 insert statements
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(17));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(16));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}
}
