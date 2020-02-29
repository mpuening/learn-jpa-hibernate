package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Imagine this is a third party class and not part of this project
 */
public class Course {
	private Long id;

	private String name;

	private Set<Student> students = new HashSet<>(0);

	private Teacher teacher;

	public Course() {
		super();
	}

	public Course(Long id, String name, Set<Student> students, Teacher teacher) {
		super();
		this.id = id;
		this.name = name;
		this.students = students;
		this.teacher = teacher;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

}
