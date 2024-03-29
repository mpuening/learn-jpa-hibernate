package io.github.learnjpahibernate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "VOYAGE")
public class Voyage extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "DEPARTURE_DATE")
	private LocalDate departureDate;

	@Column(name = "DESTINATION_DATE")
	private LocalDate destinationDate;

	@ManyToOne
	@JoinColumn(name = "DEPARTURE_PLANET_ID", nullable = false)
	private Planet departurePlanet;

	@ManyToOne
	@JoinColumn(name = "DESTINATION_PLANET_ID", nullable = false)
	private Planet destinationPlanet;

	@ManyToOne(optional = false)
	@JoinColumn(name = "SHIP_ID", nullable = false)
	private Ship ship;

	@OneToMany(mappedBy = "voyage")
	private Set<Reservation> reservations = new HashSet<>(0);
}
