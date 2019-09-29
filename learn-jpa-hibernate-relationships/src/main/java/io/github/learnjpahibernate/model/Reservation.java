package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
