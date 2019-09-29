package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "PERSON")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "TYPE", columnDefinition = "VARCHAR(20)")
public class Person extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)", nullable = false, unique = false, length = 50)
	private String name;

	// Uni-directional relationship means not having 'mappedBy' attribute
	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOME_ADDRESS_ID", nullable = false, insertable = true, updatable = true)
	private Address address;

	// CascadeType.ALL causes N+1 queries to find hailing frequencies to delete.
	// Implement a bulk delete on the relationship instead.
	@OneToMany(mappedBy = "person", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<HailingFrequency> hailingFrequencies = new HashSet<>(0);

	// CascadeType.ALL causes N+1 queries to find reservations to delete. Implement
	// a bulk delete on the relationship instead.
	@ManyToMany(mappedBy = "passengers", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<Reservation> reservations = new HashSet<>(0);
}