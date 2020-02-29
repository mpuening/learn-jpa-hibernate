package io.github.learnjpahibernate;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * We are taking over the configuration for JPA. See JPAConfiguration.
 */
@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class })
public class LearnJPAHibernateApplication {

}
