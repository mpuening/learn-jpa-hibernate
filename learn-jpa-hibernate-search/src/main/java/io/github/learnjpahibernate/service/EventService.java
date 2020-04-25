package io.github.learnjpahibernate.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;

import io.github.learnjpahibernate.model.Event;

@Service
public class EventService {

	@PersistenceContext
	protected EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<Event> searchEvents(String descriptionSearchTerm) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class)
				.get();

		org.apache.lucene.search.Query query = queryBuilder.keyword().onField("description")
				.matching(descriptionSearchTerm).createQuery();

		org.hibernate.search.jpa.FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Event.class);
		List<Event> events = jpaQuery.getResultList();
		return events;
	}
}
