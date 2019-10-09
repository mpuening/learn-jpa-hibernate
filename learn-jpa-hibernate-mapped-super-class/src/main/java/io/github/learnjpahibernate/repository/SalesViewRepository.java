package io.github.learnjpahibernate.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Sales;
import io.github.learnjpahibernate.model.SalesView;

@Repository
public interface SalesViewRepository extends JpaRepository<SalesView, Sales.SalesId> {

	SalesView findByFiscalDateAndStoreNumberAndProductName(LocalDate fiscalDate, String storeNumber,
			String productName);
}