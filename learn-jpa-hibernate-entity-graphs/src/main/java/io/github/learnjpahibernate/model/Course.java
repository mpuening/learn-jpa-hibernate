package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.Table;

import io.github.learnjpahibernate.data.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COURSE")
@NamedEntityGraphs({
		@NamedEntityGraph(name = "course-teacher-entity-graph", attributeNodes = @NamedAttributeNode("teacher")),
		@NamedEntityGraph(name = "course-students-entity-graph", attributeNodes = @NamedAttributeNode("students")),
		@NamedEntityGraph(name = "courses-students-teachers-entity-graph", attributeNodes = {
				@NamedAttributeNode("students"), @NamedAttributeNode("teacher") }) })
public class Course extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)", nullable = false, unique = true, length = 50)
	private String name;

	@ManyToMany(mappedBy = "courses", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<Student> students = new HashSet<>(0);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEACHER_ID", nullable = false)
	private Teacher teacher;

}
