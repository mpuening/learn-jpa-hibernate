package io.github.learnjpahibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FACT_SALES")
public class UpdatableSales extends Sales {

	@Column(name = "UNITS_SOLD", nullable = false)
	private Long unitsSold;

	/**
	 * Get updatable version from view.
	 */
	public UpdatableSales(SalesView from, Long unitsSold) {
		super(from.getId());
		setUnitsSold(unitsSold);
	}
}
