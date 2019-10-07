package io.github.learnjpahibernate.secondary.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.learnjpahibernate.config.TransactionManagerConfiguration;
import io.github.learnjpahibernate.primary.respository.InvoiceRepository;
import io.github.learnjpahibernate.secondary.model.LateInvoice;
import io.github.learnjpahibernate.secondary.repository.LateInvoiceRepository;

@Service
public class LateInvoiceService {

	@Autowired
	protected InvoiceRepository invoiceRepository;

	@Autowired
	protected LateInvoiceRepository lateInvoiceRepository;

	@Transactional(TransactionManagerConfiguration.SECONDARY_TRANSACTION_MANAGER_NAME)
	public int saveLateInvoices() {
		AtomicInteger count = new AtomicInteger();
		invoiceRepository.findAll().stream().forEach(invoice -> {
			LateInvoice lateInvoice = new LateInvoice(null, invoice.getName());
			lateInvoiceRepository.save(lateInvoice);
			count.incrementAndGet();
		});
		return count.get();
	}
}
