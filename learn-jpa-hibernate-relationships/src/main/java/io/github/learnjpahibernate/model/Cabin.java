package io.github.learnjpahibernate.model;

import javax.money.MonetaryAmount;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
	@JoinColumn(name = "SHIP_ID", nullable = true)
	private Ship ship;
}
