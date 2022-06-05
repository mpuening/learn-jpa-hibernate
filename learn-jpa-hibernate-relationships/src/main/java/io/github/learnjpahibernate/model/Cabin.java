package io.github.learnjpahibernate.model;

import javax.money.MonetaryAmount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import io.github.learnjpahibernate.data.AbstractEntity;
import io.github.learnjpahibernate.model.money.MonetaryAmountJsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CABIN")
public class Cabin extends AbstractEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Column(name = "DECK_LEVEL", columnDefinition = "INTEGER", nullable = false)
	private int deckLevel;

	@NotNull
	@Column(name = "BED_COUNT", columnDefinition = "INTEGER", nullable = false)
	private int bedCount;

	@NotNull
	@MonetaryAmountJsonSerialize
	@Type(type = "io.github.learnjpahibernate.model.money.MonetaryAmountUserType")
	@Columns(columns = { @Column(name = "PRICE"), @Column(name = "CURRENCY") })
	private MonetaryAmount price;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "SHIP_ID")
	private Ship ship;
}
