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
import javax.persistence.PrePersist;
import javax.persistence.Table;

import io.github.learnjpahibernate.data.AbstractEntity;
import io.github.learnjpahibernate.model.HailingFrequency.HailingFrequencyId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HAILING_FREQUENCY")
public class HailingFrequency extends AbstractEntity<HailingFrequencyId> {

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "personId", column = @Column(name = "PERSON_ID", nullable = false, updatable = false)),
			@AttributeOverride(name = "frequency", column = @Column(name = "FREQUENCY", nullable = false, updatable = false)) })
	private HailingFrequencyId id;

	// Note: even though advertised as a unidirectional relationship, it is better
	// to model it as bidirectional... it is the only way to avoid having to require
	// a "generated id column", and the corresponding update statements typical with
	// Hibernate's OneToMany relationship implementation.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PERSON_ID", nullable = false, insertable = false, updatable = false)
	private Person person;

	@PrePersist
	protected void setForeignKeyBeforeSave() {
		if (this.getId() != null && getId().getPersonId() == null && getPerson() != null) {
			// Setting foreign key relationship
			this.getId().setPersonId(getPerson().getId());
		}
		// if no id is there, a null key violation will probably occur...
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	public static class HailingFrequencyId implements java.io.Serializable {
		private static final long serialVersionUID = -8796025224129208865L;

		@Column(name = "PERSON_ID", nullable = false, updatable = false)
		private Long personId;

		@Column(name = "FREQUENCY", columnDefinition = "VARCHAR(50)", nullable = false, length = 50)
		private String frequency;
	}
}