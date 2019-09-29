package io.github.learnjpahibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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