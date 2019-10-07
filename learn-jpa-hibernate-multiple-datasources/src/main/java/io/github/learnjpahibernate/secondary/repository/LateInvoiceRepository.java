package io.github.learnjpahibernate.secondary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.secondary.model.LateInvoice;

@Repository
public interface LateInvoiceRepository extends JpaRepository<LateInvoice, Long> {

}