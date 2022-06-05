package io.github.learnjpahibernate.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FACT_SALES_VIEW")
public class SalesView extends Sales {

	@Column(name = "FISCAL_DATE", nullable = false)
	private LocalDate fiscalDate;

	@Column(name = "STORE_NUMBER", columnDefinition = "VARCHAR(100)", nullable = false, length = 100, updatable = false)
	private String storeNumber;

	@Column(name = "PRODUCT_NAME", columnDefinition = "VARCHAR(100)", nullable = false, length = 100, updatable = false)
	private String productName;

	@Column(name = "UNITS_SOLD", nullable = false)
	private Long unitsSold;

	/**
	 * This entity bean is from a view, and thus is not updatable. Therefore we have
	 * a conversion method to return a version that can be updated.
	 */
	public UpdatableSales asUpdatable(Long unitsSold) {
		return new UpdatableSales(this, unitsSold);
	}
}
