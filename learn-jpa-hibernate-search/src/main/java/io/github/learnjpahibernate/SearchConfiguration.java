package io.github.learnjpahibernate;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Configuration
public class SearchConfiguration implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	public void onApplicationEvent(ApplicationReadyEvent event) {
		SearchSession searchSession = Search.session(entityManager);
		MassIndexer indexer = searchSession
				.massIndexer()
				.idFetchSize(100)
				.batchSizeToLoadObjects(25)
				.threadsToLoadObjects(2);
		try {
			indexer.startAndWait();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
