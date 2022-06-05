package io.github.learnjpahibernate.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PERSON")
@DiscriminatorValue("CAPTAIN")
public class Captain extends Person {

	// Lombok doesn't support this c'tor
	public Captain(Long id, String name, Address address, Set<HailingFrequency> hailingFrequencies,
			Set<Reservation> reservations, Ship ship) {
		super(id, name, address, hailingFrequencies, reservations);
		this.ship = ship;
	}

	// Bi-directional relationship has 'mappedBy' attribute
	@OneToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "SHIP_ID")
	private Ship ship;

}