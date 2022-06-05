package io.github.learnjpahibernate.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;

import io.github.learnjpahibernate.data.AbstractEntity;
import io.github.learnjpahibernate.model.Sales.SalesId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Sales extends AbstractEntity<SalesId> {

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "dateId", column = @Column(name = "DATE_ID", nullable = false, updatable = false)),
			@AttributeOverride(name = "storeId", column = @Column(name = "STORE_ID", nullable = false, updatable = false)),
			@AttributeOverride(name = "productId", column = @Column(name = "PRODUCT_ID", nullable = false, updatable = false)) })
	private SalesId id;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	public static class SalesId implements java.io.Serializable {
		private static final long serialVersionUID = -8796025224129208865L;

		@Column(name = "DATE_ID", nullable = false, updatable = false)
		private Long dateId;

		@Column(name = "STORE_ID", nullable = false, updatable = false)
		private Long storeId;

		@Column(name = "PRODUCT_ID", nullable = false, updatable = false)
		private Long productId;
	}
}
