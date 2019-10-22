package io.github.learnjpahibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.model.Event;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.hamcrest.DataSourceAssertMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected DataSource dataSource;

	@PersistenceContext
	protected EntityManager entityManager;

	@Test
	@Transactional
	public void testEventBatching() {
		assertNotNull(dataSource);
		assertTrue(dataSource instanceof ProxyTestDataSource);
		ProxyTestDataSource proxyDataSource = (ProxyTestDataSource) dataSource;
		proxyDataSource.reset();

		int BATCH_SIZE = 50;
		for (int i = 0; i < 5000; i++) {
			Event event = new Event();
			event.setDescription("New event for " + UUID.randomUUID().toString());
			event.setCreatedBy("USER");
			event.setCreatedDate(LocalDateTime.now());
			entityManager.persist(event);
			// To save memory, considering flushing and clearing the entity manager
			if ((i+1) % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		entityManager.flush();
		entityManager.clear();

		// With batching, insert count is decreased: 5000 / 50 = 100 insert stmts
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.executionCount(201));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.insertCount(100));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.selectCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.updateCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.deleteCount(0));
		Assert.assertThat(proxyDataSource, DataSourceAssertMatchers.otherCount(101));
	}
}
