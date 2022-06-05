package io.github.learnjpahibernate.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import io.github.learnjpahibernate.model.Course;
import io.github.learnjpahibernate.projection.CourseCompactShape;
import io.github.learnjpahibernate.projection.CourseFullShape;
import io.github.learnjpahibernate.projection.CourseStudentsShape;
import io.github.learnjpahibernate.projection.CourseTeacherShape;

@Service
public class CourseService {

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected ProjectionFactory projectionFactory;

	@SuppressWarnings("unchecked")
	public List<Course> getCoursesPoorly() {
		List<Course> courses = entityManager.createQuery("SELECT c FROM Course c").getResultList();
		return courses;

	}

	@SuppressWarnings("unchecked")
	public List<Object> getCourses(String projection) {
		String entityGraph = null;
		final Class<?>[] projectionClass = new Class[] { CourseCompactShape.class };
		if ("teacher".equalsIgnoreCase(projection)) {
			entityGraph = "course-teacher-entity-graph";
			projectionClass[0] = CourseTeacherShape.class;
		} else if ("student".equalsIgnoreCase(projection)) {
			entityGraph = "course-students-entity-graph";
			projectionClass[0] = CourseStudentsShape.class;
		} else if ("full".equalsIgnoreCase(projection)) {
			entityGraph = "courses-students-teachers-entity-graph";
			projectionClass[0] = CourseFullShape.class;
		}
		Query query = entityManager.createQuery("SELECT c FROM Course c");
		if (entityGraph != null) {
			EntityGraph<?> graph = entityManager.getEntityGraph(entityGraph);
			query.setHint("jakarta.persistence.loadgraph", graph);
		}
		return ((List<Course>) query.getResultList()).stream().map(course -> {
			return projectionFactory.createProjection(projectionClass[0], course);
		}).collect(Collectors.toList());
	}
}
