package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "BOOK")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {
	@Id
	@Column(name = "ISBN_NUMBER")
	@EqualsAndHashCode.Include
	private String isbn;

	@Column(name = "NAME")
	private String name;

	@ManyToMany
	@JoinTable(name = "BOOK_AUTHOR", joinColumns = @JoinColumn(name = "ISBN_NUMBER"), inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID"))
	private Set<Author> authors = new HashSet<>();
}
