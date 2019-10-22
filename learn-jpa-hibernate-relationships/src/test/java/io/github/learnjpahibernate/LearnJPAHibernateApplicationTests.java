package io.github.learnjpahibernate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
public class LearnJPAHibernateApplicationTests {

	@Test
	@Sql("/test-case-data.sql")
	public void contextLoads() {
	}
}
