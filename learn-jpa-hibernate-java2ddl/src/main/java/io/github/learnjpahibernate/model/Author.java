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

import io.github.learnjpahibernate.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "AUTHOR")
public class Author extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@ManyToMany(mappedBy = "authors")
	private Set<Book> books = new HashSet<>(0);

	@OneToMany(mappedBy = "id.authorId")
	private Set<Preference> preferences = new HashSet<>(0);
}