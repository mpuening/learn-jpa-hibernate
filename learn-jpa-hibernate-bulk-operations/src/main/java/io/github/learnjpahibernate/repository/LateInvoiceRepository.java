package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.LateInvoice;

@Repository
public interface LateInvoiceRepository extends JpaRepository<LateInvoice, Long> {

}