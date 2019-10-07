package io.github.learnjpahibernate.primary.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.primary.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

}