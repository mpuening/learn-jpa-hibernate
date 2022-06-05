package io.github.learnjpahibernate.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;

import io.github.learnjpahibernate.model.Event;

@Service
public class EventService {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<Event> searchEvents(String descriptionSearchTerm) {
		SearchSession searchSession = Search.session(entityManager);
		SearchResult<Event> result = searchSession.search(Event.class)

				.where(f -> f.match()

						.field("description")

						.matching(descriptionSearchTerm))

				.fetchAll();

		List<Event> events = result.hits();
		return events;
	}
}
