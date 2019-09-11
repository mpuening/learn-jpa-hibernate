package io.github.learnjpahibernate.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "PREFERENCE")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Preference {
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "authorId", column = @Column(name = "AUTHOR_ID", nullable = false, updatable = false)),
			@AttributeOverride(name = "name", column = @Column(name = "NAME", nullable = false, updatable = false)) })
	@EqualsAndHashCode.Include
	private PreferenceId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTHOR_ID", nullable = false, insertable = false, updatable = false)
	private Author author;

	@Column(name = "VALUE")
	private String value;

	@Data
	@EqualsAndHashCode
	@Embeddable
	public static class PreferenceId implements java.io.Serializable {
		private static final long serialVersionUID = 8631701868884857104L;
		@Column(name = "AUTHOR_ID", nullable = false, updatable = false)
		private long authorId;

		@Column(name = "NAME", nullable = false, updatable = false)
		private String name;
	}
}
