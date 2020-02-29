package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Imagine this is a third party class and not part of this project
 */
public class Student {
	private Long id;

	private String name;

	private Set<Course> courses = new HashSet<>(0);

	public Student() {
		super();
	}

	public Student(Long id, String name, Set<Course> courses) {
		super();
		this.id = id;
		this.name = name;
		this.courses = courses;
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

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

}
