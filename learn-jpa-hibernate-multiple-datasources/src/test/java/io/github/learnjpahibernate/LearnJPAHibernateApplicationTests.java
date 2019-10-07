package io.github.learnjpahibernate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.learnjpahibernate.secondary.service.LateInvoiceService;

@RunWith(SpringRunner.class)
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
