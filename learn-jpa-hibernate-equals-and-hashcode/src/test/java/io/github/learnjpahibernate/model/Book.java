package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
	private Set<Author> authors = new HashSet<>(0);

	@Override
	@Transient
	public String getId() {
		return getIsbn();
	}
}
