package io.github.learnjpahibernate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfiguration {

	@Autowired
	protected EntityManagerFactory entityManagerFactory;

	@Bean
	public FullTextEntityManager fullTextEntityManager() {
		return Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
	}

	@PostConstruct
	public void index() throws InterruptedException {
		// Should this be async?
		fullTextEntityManager().createIndexer().startAndWait();
	}
}
