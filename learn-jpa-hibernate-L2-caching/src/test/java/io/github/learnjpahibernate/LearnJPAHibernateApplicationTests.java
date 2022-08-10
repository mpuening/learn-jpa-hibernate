package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.EventType;
import javax.cache.management.CacheStatisticsMXBean;
import javax.cache.spi.CachingProvider;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.model.Course;
import io.github.learnjpahibernate.repository.CourseRepository;
import io.github.learnjpahibernate.repository.TeacherRepository;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

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
	public void testCourseEhCache() throws Exception {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

	    CachingProvider provider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
	    CacheManager cacheManager =
	    		provider.getCacheManager(
	    				this.getClass().getResource("/ehcache.xml").toURI(),
	    				provider.getDefaultClassLoader());
		assertNotNull(cacheManager);

		Cache<Object, Object> courseCache = cacheManager.getCache("io.github.learnjpahibernate.model.Course");
		assertNotNull(courseCache);
		SimpleCacheEntryCounter<Object, Object> courseCounter = registerJsr107CacheEntryCounter(courseCache);

		Cache<Object, Object> courseStudentsCache = cacheManager.getCache("io.github.learnjpahibernate.model.Course.students");
		assertNotNull(courseStudentsCache);
		SimpleCacheEntryCounter<Object, Object> studentsCounter = registerJsr107CacheEntryCounter(courseStudentsCache);

		Cache<Object, Object> queryResultsCache = cacheManager.getCache("default-query-results-region");
		assertNotNull(queryResultsCache);
		SimpleCacheEntryCounter<Object, Object> queryCounter = registerJsr107CacheEntryCounter(queryResultsCache);
		
		assertNotNull(courseRepository);

		Course algebra = courseRepository.findById(1L).orElse(null);
		assertNotNull(algebra);
		assertEquals("Algebra", algebra.getName());
		assertEquals(1, algebra.getStudents().size());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName(algebra.getTeacher().getName()).getName());

		int size = courseCounter.getCount();
		assertEquals(1, size);
		size = studentsCounter.getCount();
		assertEquals(1, size);
		size = queryCounter.getCount();
		assertEquals(1, size);

		algebra = courseRepository.findById(1L).orElse(null);
		assertNotNull(algebra);
		assertEquals("Algebra", algebra.getName());
		assertEquals(1, algebra.getStudents().size());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName(algebra.getTeacher().getName()).getName());
		size = courseCounter.getCount();
		assertEquals(1, size);
		size = studentsCounter.getCount();
		assertEquals(1, size);
		size = queryCounter.getCount();
		assertEquals(1, size);

		Course geometry = courseRepository.findById(2L).orElse(null);
		assertNotNull(geometry);
		assertEquals("Geometry", geometry.getName());
		assertEquals(2, geometry.getStudents().size());
		assertEquals("Ms. Boyd", teacherRepository.findTeacherByName(geometry.getTeacher().getName()).getName());
		size = courseCounter.getCount();
		assertEquals(2, size);
		size = studentsCounter.getCount();
		assertEquals(2, size);
		size = queryCounter.getCount();
		assertEquals(1, size);

		// Six queries should take place, two for courses (one cached), two for
		// students (one cached), two for teachers (one relationship, one select)
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	@Test
	@Transactional
	public void testEhCacheQueries() throws Exception {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

	    CachingProvider provider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
	    CacheManager cacheManager =
	    		provider.getCacheManager(
	    				this.getClass().getResource("/ehcache.xml").toURI(),
	    				provider.getDefaultClassLoader());
		assertNotNull(cacheManager);

		Cache<Object, Object> queryResultsCache = cacheManager.getCache("default-query-results-region");
		assertNotNull(queryResultsCache);
		SimpleCacheEntryCounter<Object, Object> counter = registerJsr107CacheEntryCounter(queryResultsCache);
		
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

		// This should be 1, but we don't get eviction events
		assertEquals(6, counter.getCount());
		
		// Six queries take place because the cache size is 1 (see ehcache.xml/ TeacherRepository.java)
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(6));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		MatcherAssert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
	}

	protected void printStats() throws Exception {
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectInstance> cacheBeans = beanServer
				.queryMBeans(ObjectName.getInstance("javax.cache:type=CacheStatistics,CacheManager=*,Cache=*"), null);
		for (ObjectInstance cacheBean : cacheBeans) {
			CacheStatisticsMXBean cacheStatisticsMXBean = MBeanServerInvocationHandler
					.newProxyInstance(beanServer, cacheBean.getObjectName(), CacheStatisticsMXBean.class, false);

			System.out.println("Name " + cacheBean.getObjectName().getKeyProperty("Cache"));
			System.out.println("Gets " + cacheStatisticsMXBean.getCacheGets());
			System.out.println("Hits " + cacheStatisticsMXBean.getCacheHits());
			System.out.println("Misses " + cacheStatisticsMXBean.getCacheMisses());
			System.out.println("Removals " + cacheStatisticsMXBean.getCacheRemovals());
			System.out.println("Evictions " + cacheStatisticsMXBean.getCacheEvictions());
			System.out.println("AvgGetTime " + cacheStatisticsMXBean.getAverageGetTime());
			System.out.println("AvgPutTime " + cacheStatisticsMXBean.getAveragePutTime());
			System.out.println("AvgRemoveTime " + cacheStatisticsMXBean.getAverageRemoveTime());
		}
	}

	protected SimpleCacheEntryCounter<Object, Object> registerJsr107CacheEntryCounter(Cache<Object, Object> cache) {
		SimpleCacheEntryCounter<Object, Object> listener = new SimpleCacheEntryCounter<Object, Object>();
		SimpleCacheEntryEventFilter<Object, Object> filter = new SimpleCacheEntryEventFilter<Object, Object>();
		MutableCacheEntryListenerConfiguration<Object, Object> config = new MutableCacheEntryListenerConfiguration<Object, Object>(
				FactoryBuilder.factoryOf(listener), FactoryBuilder.factoryOf(filter), false, true);
		cache.registerCacheEntryListener(config);
		return listener;
	}

	/**
	 * Poor man's cache entry counter. This counter really doesn't work because we don't get eviction events. Should you
	 * want a more real implementation, check out EhCache's implementation of listeners which do support eviction events.
	 * 
	 * See https://www.baeldung.com/spring-boot-ehcache
	 *
	 * This class's usefulness is to prove that the second level cache is working.
	 */
	public class SimpleCacheEntryCounter<K, V> implements CacheEntryCreatedListener<K, V>, Serializable, CacheEntryRemovedListener<K, V>, CacheEntryExpiredListener<K, V> {
		private static final long serialVersionUID = 1L;

		private int count = 0;

		public int getCount() {
			return count;
		}

		@Override
		public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			processEvents(events);
		}

		@Override
		public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			processEvents(events);
		}

		@Override
		public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			processEvents(events);			
		}
		
		private void processEvents(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
			for (CacheEntryEvent<? extends K, ? extends V> event : events) {
				// Count will drift because JSR 107 doesn't support Eviction events
				if (EventType.CREATED.equals(event.getEventType())) {
					count++;
				}
				else {
					count--;
				}
			}
		}
	}

	public class SimpleCacheEntryEventFilter<K, V> implements CacheEntryEventFilter<K, V>, Serializable {
		private static final long serialVersionUID = 1L;
		private final EnumSet<EventType> allowed = EnumSet.of(EventType.CREATED, EventType.REMOVED, EventType.EXPIRED);

		@Override
		public boolean evaluate(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException {
			return allowed.contains(event.getEventType());
		}
	}
}
