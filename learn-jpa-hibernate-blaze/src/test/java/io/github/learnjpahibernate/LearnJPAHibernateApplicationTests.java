package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.sql.DataSource;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import com.blazebit.persistence.CriteriaBuilderFactory;

import io.github.learnjpahibernate.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@SpringBootTest
public class LearnJPAHibernateApplicationTests {
	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected TransactionTemplate transactionTemplate;

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected CriteriaBuilderFactory criteriaBuilderFactory;

	@Test
	public void testBlazeQuery() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(criteriaBuilderFactory);

		transactionTemplate.execute(status -> {
			// Select all students, 20 per page
			List<Student> students = criteriaBuilderFactory
					.create(entityManager, Student.class)
					.orderByAsc("id")
					.page(0, 20)
					.getResultList();
			assertEquals(10, students.size());
			students.forEach(s -> System.out.println(s.getName()));
			return null;
		});

		// One select statements for students
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testBlazeQueryWithWhereClause() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(criteriaBuilderFactory);

		transactionTemplate.execute(status -> {
			// Select all students named Fred
			List<Student> students = criteriaBuilderFactory
					.create(entityManager, Student.class)
					.where("name").eq("Fred")
					.getResultList();
			assertEquals(1, students.size());
			assertEquals("Fred", students.get(0).getName());
			return null;
		});

		// One select statements for students
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testBlazeQueryWithCompoundWhereClause() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(criteriaBuilderFactory);

		transactionTemplate.execute(status -> {
			// Select all students according to conditions
			List<Student> students = criteriaBuilderFactory
					.create(entityManager, Student.class)
					.whereOr()
						.whereAnd()
							.where("id").gt(2)
							.where("id").lt(5)
						.endAnd()
						.whereAnd()
							.where("name").eq("Jack")
						.endAnd()
					.endOr()
					.getResultList();
			assertEquals(3, students.size());
			return null;
		});

		// One select statements for students
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testBlazeQueryWithJoin() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(criteriaBuilderFactory);

		transactionTemplate.execute(status -> {
			// Select all students with courses
			List<Student> students = criteriaBuilderFactory
					.create(entityManager, Student.class)
					.innerJoin("courses", "c")
					.getResultList();
			// Only Alice and Beth signed up for courses (4 each)
			assertEquals(2, students.size());
			assertEquals(4, students.get(0).getCourses().size());
			assertEquals(4, students.get(1).getCourses().size());
			return null;
		});

		// One select statements for students, then two additional for courses (one each student)
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(3));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	public void testBlazeQueryWithJoinAndFetch() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(criteriaBuilderFactory);

		transactionTemplate.execute(status -> {
			// Select all students with courses, fetching courses in same query, avoiding 1+N problem
			List<Student> students = criteriaBuilderFactory
					.create(entityManager, Student.class)
					.fetch("courses")
					.innerJoin("courses", "c")
					.getResultList();
			// Only Alice and Beth signed up for courses (4 each)
			assertEquals(2, students.size());
			assertEquals(4, students.get(0).getCourses().size());
			assertEquals(4, students.get(1).getCourses().size());
			return null;
		});

		// One select statements for students
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(1));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}
}
