package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "RESERVATION")
public class Reservation extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "VOYAGE_ID", nullable = false)
	private Voyage voyage;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "RESERVATION_CABIN", joinColumns = @JoinColumn(name = "RESERVATION_ID"), inverseJoinColumns = @JoinColumn(name = "CABIN_ID"))
	private Set<Cabin> cabins = new HashSet<>(0);

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "RESERVATION_PERSON", joinColumns = @JoinColumn(name = "RESERVATION_ID"), inverseJoinColumns = @JoinColumn(name = "PERSON_ID"))
	private Set<Person> passengers = new HashSet<>(0);
}
