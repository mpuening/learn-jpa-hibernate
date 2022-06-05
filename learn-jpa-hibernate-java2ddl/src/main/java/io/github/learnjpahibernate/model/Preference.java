package io.github.learnjpahibernate.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import io.github.learnjpahibernate.data.AbstractEntity;
import io.github.learnjpahibernate.model.Preference.PreferenceId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PREFERENCE")
public class Preference extends AbstractEntity<PreferenceId> {
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "authorId", column = @Column(name = "AUTHOR_ID", nullable = false, updatable = false)),
			@AttributeOverride(name = "name", column = @Column(name = "NAME", nullable = false, updatable = false)) })
	private PreferenceId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTHOR_ID", nullable = false, insertable = false, updatable = false)
	private Author author;

	@Column(name = "PREF_VALUE")
	private String value;

	@Data
	@Embeddable
	public static class PreferenceId implements java.io.Serializable {
		private static final long serialVersionUID = 8631701868884857104L;
		@Column(name = "AUTHOR_ID", nullable = false, updatable = false)
		private Long authorId;

		@Column(name = "NAME", nullable = false, updatable = false)
		private String name;
	}
}
