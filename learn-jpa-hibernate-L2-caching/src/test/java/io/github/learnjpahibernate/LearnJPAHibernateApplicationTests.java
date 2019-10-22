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
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.model.Course;
import io.github.learnjpahibernate.repository.CourseRepository;
import io.github.learnjpahibernate.repository.TeacherRepository;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected TeacherRepository teacherRepository;

	@Autowired
	protected CourseRepository courseRepository;

	@Test
	@Transactional
	public void testCourseEhCache() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		assertNotNull(courseRepository);

		Course algebra = courseRepository.findById(1L).orElse(null);
		assertNotNull(algebra);
		assertEquals("Algebra", algebra.getName());
		assertEquals(1, algebra.getStudents().size());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName(algebra.getTeacher().getName()).getName());
		CacheManager cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);
		assertNotNull(cacheManager);
		Cache courseCache = cacheManager.getCache("io.github.learnjpahibernate.model.Course");
		assertNotNull(courseCache);
		int size = courseCache.getSize();
		assertEquals(1, size);
		Cache courseStudentsCache = cacheManager.getCache("io.github.learnjpahibernate.model.Course.students");
		assertNotNull(courseStudentsCache);
		size = courseStudentsCache.getSize();
		assertEquals(1, size);
		Cache queryCache = cacheManager.getCache("default-query-results-region");
		assertNotNull(queryCache);
		size = queryCache.getSize();
		assertEquals(1, size);

		algebra = courseRepository.findById(1L).orElse(null);
		assertNotNull(algebra);
		assertEquals("Algebra", algebra.getName());
		assertEquals(1, algebra.getStudents().size());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName(algebra.getTeacher().getName()).getName());
		size = courseCache.getSize();
		assertEquals(1, size);
		size = courseStudentsCache.getSize();
		assertEquals(1, size);
		size = queryCache.getSize();
		assertEquals(1, size);

		Course geometry = courseRepository.findById(2L).orElse(null);
		assertNotNull(geometry);
		assertEquals("Geometry", geometry.getName());
		assertEquals(2, geometry.getStudents().size());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName(geometry.getTeacher().getName()).getName());
		size = courseCache.getSize();
		assertEquals(2, size);
		size = courseStudentsCache.getSize();
		assertEquals(2, size);
		size = queryCache.getSize();
		assertEquals(1, size);

		// Six queries should take place, two for courses (one cached), two for
		// students (one cached), two for teachers (one relationship, one select)
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	@Transactional
	public void testEhCacheQueries() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		// Every other query is cached
		assertEquals("Ms. Crabapple", teacherRepository.findTeacherByName("Ms. Crabapple").getName());
		assertEquals("Ms. Crabapple", teacherRepository.findTeacherByName("Ms. Crabapple").getName());

		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName("Ms. Boyd").getName());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName("Ms. Boyd").getName());

		assertEquals("Mr. Cooper", teacherRepository.findTeacherByName("Mr. Cooper").getName());
		assertEquals("Mr. Cooper", teacherRepository.findTeacherByName("Mr. Cooper").getName());

		// These queries evict the previous ones
		assertEquals("Ms. Crabapple", teacherRepository.findTeacherByName("Ms. Crabapple").getName());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName("Ms. Boyd").getName());
		assertEquals("Mr. Cooper", teacherRepository.findTeacherByName("Mr. Cooper").getName());

		// Six queries take place because the cache size is 1 (see ehcache.xml)
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(6));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}
}
