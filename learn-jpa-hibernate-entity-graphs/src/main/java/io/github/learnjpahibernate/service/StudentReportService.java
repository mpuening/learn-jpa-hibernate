package io.github.learnjpahibernate.service;

import java.util.List;
import java.util.StringJoiner;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.model.Student;
import io.github.learnjpahibernate.repository.StudentCoursesRepository;
import io.github.learnjpahibernate.repository.StudentCoursesTeachersRepository;
import io.github.learnjpahibernate.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class StudentReportService {

	@Autowired
	protected StudentRepository studentRepository;

	@Autowired
	protected StudentCoursesRepository studentCoursesRepository;

	@Autowired
	protected StudentCoursesTeachersRepository studentCoursesTeachersRepository;

	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * This method demonstrates the N+1 problem...
	 */
	public String generateReportPoorly() {
		return generateReport(studentRepository.findAll());
	}

	/**
	 * This method resolves the N+1 problem with a join query
	 */
	public String generateReportWithJoinQuery() {
		List<Student> students = entityManager
				.createQuery("select distinct s from Student s " + "join fetch s.courses c " + "join fetch c.teacher",
						Student.class)
				.getResultList();
		return generateReport(students);
	}

	/**
	 * This method resolves the N+1 problem with a join query using API
	 */
	public String generateReportWithCriteriaQuery() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Student> q = cb.createQuery(Student.class).distinct(true);
		Root<Student> s = q.from(Student.class);
		s.fetch("courses").fetch("teacher");
		CriteriaQuery<Student> criteria = q.select(s);

		TypedQuery<Student> query = entityManager.createQuery(criteria);
		List<Student> students = query.getResultList();

		return generateReport(students);
	}

	/**
	 * This method partially resolves the N+1 problem with a partial entity graph
	 */
	public String generateReportWithPartialEntityGraph() {
		return generateReport(studentCoursesRepository.findAll());
	}

	/**
	 * This method resolves the N+1 problem with a full entity graph
	 */
	public String generateReportWithFullEntityGraph() {
		return generateReport(studentCoursesTeachersRepository.findAll());
	}

	/**
	 * This method resolves the N+1 problem with a dynamic entity graph query
	 */
	@SuppressWarnings("unchecked")
	public String generateReportWithDynamicEntityGraph() {
		EntityGraph<Student> graph = entityManager.createEntityGraph(Student.class);
		graph.addAttributeNodes("courses");
		graph.addSubgraph("courses").addAttributeNodes("teacher");

		List<Student> students = entityManager.createQuery("SELECT s FROM Student s")
				.setHint("javax.persistence.loadgraph", graph).getResultList();

		return generateReport(students);
	}

	protected String generateReport(List<Student> students) {
		final StringJoiner report = new StringJoiner("\n");
		students.forEach(student -> {
			student.getCourses().stream().forEach(course -> {
				report.add(String.format("%-20s %-20s %-20s", student.getName(), course.getName(),
						course.getTeacher().getName()));
			});
		});
		return report.toString();
	}
}
