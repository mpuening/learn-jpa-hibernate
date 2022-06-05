package io.github.learnjpahibernate.service;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.model.Invoice;

@Service
@Transactional
public class InvoiceService {
	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * Example update statement
	 */
	public int reassignInvoices(String oldManager, String newManager) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<Invoice> update = criteriaBuilder.createCriteriaUpdate(Invoice.class);
		Root<Invoice> root = update.from(Invoice.class);
		update.set(root.get("accountManager"), newManager);
		update.where(criteriaBuilder.equal(root.get("accountManager"), oldManager));
		int rowsUpdated = entityManager.createQuery(update).executeUpdate();
		return rowsUpdated;
	}

	/**
	 * Example delete statement
	 */
	public int removeInvoices(String companyName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<Invoice> update = criteriaBuilder.createCriteriaDelete(Invoice.class);
		Root<Invoice> root = update.from(Invoice.class);
		update.where(criteriaBuilder.equal(root.get("name"), companyName));
		int rowsUpdated = entityManager.createQuery(update).executeUpdate();
		return rowsUpdated;
	}

	/**
	 * Example insert from select statement, requires a native query
	 */
	public int copyLateInvoices(String companyName) {
		String insertTable = "LATE_INVOICE";
		Map<String, Object> columnMappings = new LinkedHashMap<>();
		columnMappings.put("ID", "id");
		columnMappings.put("NAME", "name");
		columnMappings.put("REVIEW_DATE", "2019-12-31");
		Specification<Invoice> specification = ((invoice, cq, cb) -> cb.like(invoice.get("name"),
				"%" + companyName + "%"));
		Query insertFromSelect = InsertFromSelect.createNativeQuery(insertTable, columnMappings, entityManager,
				Invoice.class, specification);
		int rowsUpdated = insertFromSelect.executeUpdate();
		return rowsUpdated;
	}
}
