package io.github.learnjpahibernate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LearnJPAHibernateApplicationTests {

	@Test
	@Sql("/test-case-data.sql")
	public void contextLoads() {
	}
}
