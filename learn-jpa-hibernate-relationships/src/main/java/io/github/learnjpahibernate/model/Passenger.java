package io.github.learnjpahibernate.model;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PASSENGER")
@DiscriminatorValue("PASSENGER")
public class Passenger extends Person {

	// Lombok doesn't support this c'tor
	public Passenger(Long id, String name, Address address, Set<HailingFrequency> hailingFrequencies,
			Set<Reservation> reservations) {
		super(id, name, address, hailingFrequencies, reservations);
	}

}