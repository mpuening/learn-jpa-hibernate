package io.github.learnjpahibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import lombok.Data;

/**
 * This entity uses lombok to generate a bad equals and hashCode method. Make
 * sure you use @Getter and @Setter instead when using lombok.
 */
@Entity
@Data
@Table(name = "LOMBOK")
public class BadLombokEntity implements Persistable<String> {
	@Id
	@Column(name = "ISBN_NUMBER")
	private String isbn;

	@Column(name = "NAME")
	private String name;

	@Override
	@Transient
	public String getId() {
		return getIsbn();
	}

	@Transient
	public boolean isNew() {
		return getId() == null;
	}
}