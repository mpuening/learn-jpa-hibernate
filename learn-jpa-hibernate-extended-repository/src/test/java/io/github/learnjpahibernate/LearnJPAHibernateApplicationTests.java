package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.learnjpahibernate.model.Course;
import io.github.learnjpahibernate.model.Student;
import io.github.learnjpahibernate.repository.CourseRepository;
import io.github.learnjpahibernate.repository.StudentRepository;

@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected StudentRepository studentRepository;

	@Autowired
	protected CourseRepository courseRepository;

	@Test
	public void testExtendedStudentRepository() {
		List<Student> students = studentRepository.findByPropertyContainsValue("name", "re");
		// Greta and Fred
		assertEquals(2, students.size());
	}

	@Test
	public void testExtendedCourseRepository() {
		List<Course> courses = courseRepository.findByPropertyContainsValue("name", "History");
		// US and World History
		assertEquals(2, courses.size());
	}
}
