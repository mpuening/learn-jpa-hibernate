package io.github.learnjpahibernate.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	@JoinColumn(name = "HOME_ADDRESS_ID", nullable = false, insertable = true)
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