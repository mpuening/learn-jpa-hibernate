package io.github.learnjpahibernate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "ADDRESS")
public class Address extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "STREET", columnDefinition = "VARCHAR(50)", nullable = false, unique = false, length = 50)
	private String street;

	@Column(name = "CITY", columnDefinition = "VARCHAR(50)", nullable = false, unique = false, length = 50)
	private String city;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PLANET_ID", nullable = false)
	private Planet planet;
}