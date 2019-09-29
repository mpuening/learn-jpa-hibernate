package io.github.learnjpahibernate.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
	@JoinColumn(name = "SHIP_ID", nullable = true)
	private Ship ship;

}