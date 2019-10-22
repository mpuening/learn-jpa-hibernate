package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.learnjpahibernate.secondary.service.LateInvoiceService;

@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected LateInvoiceService lateInvoiceService;

	@Test
	public void testSaveLateInvoices() {
		int count = lateInvoiceService.saveLateInvoices();
		assertEquals(4, count);
	}
}
