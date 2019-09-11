package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "AUTHOR")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@EqualsAndHashCode.Include
	private Long id;

	@Column(name = "NAME")
	private String name;

	@ManyToMany(mappedBy = "authors")
	private Set<Book> books = new HashSet<>();

	@OneToMany(mappedBy = "id.authorId")
	private Set<Preference> preferences = new HashSet<>();
}