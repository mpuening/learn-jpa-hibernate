package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "SHIP")
public class Ship extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)", nullable = false, unique = false, length = 50)
	private String name;

	@Column(name = "SHIP_CLASS", columnDefinition = "VARCHAR(50)", nullable = false, unique = false, length = 50)
	private String shipClass;

	@OneToOne(mappedBy = "ship", optional = true)
	private Captain captain;

	@OneToMany(mappedBy = "ship", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<Cabin> cabins = new HashSet<>(0);

	/**
	 * TODO: Is this a Spring HATEOS Bug? Starting with Spring Boot 2.5, POSTs of new objects
	 * with links to other existing objects were handled automatically. Now a String c'tor
	 * is called, and logic must be implemented to deal with notation of the string.
	 */
	public Ship(String id) {
		this.id = Long.valueOf(id.replace("/ships/", ""));
	}
}
