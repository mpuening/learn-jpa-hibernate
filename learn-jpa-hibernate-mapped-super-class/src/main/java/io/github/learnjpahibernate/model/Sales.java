package io.github.learnjpahibernate.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

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
public class Sales extends AbstractEntity<SalesId> {

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
