package io.github.learnjpahibernate.projection;

import java.util.Set;

import io.github.learnjpahibernate.model.Student;

public interface CourseStudentsShape extends CourseCompactShape {
	Set<Student> getStudents();
}
