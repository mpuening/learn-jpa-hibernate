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
import javax.persistence.Transient;

import io.github.learnjpahibernate.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "BOOK")
public class Book extends AbstractEntity<String> {
	@Id
	@Column(name = "ISBN_NUMBER")
	private String isbn;

	@Column(name = "NAME")
	private String name;

	@ManyToMany
	@JoinTable(name = "BOOK_AUTHOR", joinColumns = @JoinColumn(name = "ISBN_NUMBER"), inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID"))
	private Set<Author> authors = new HashSet<>();

	@Override
	@Transient
	public String getId() {
		return getIsbn();
	}
}
