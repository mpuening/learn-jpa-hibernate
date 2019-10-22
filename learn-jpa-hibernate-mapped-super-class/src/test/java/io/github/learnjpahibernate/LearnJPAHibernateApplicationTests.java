package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.learnjpahibernate.model.SalesView;
import io.github.learnjpahibernate.repository.SalesRepository;
import io.github.learnjpahibernate.repository.SalesViewRepository;

@SpringBootTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected SalesViewRepository salesViewRepository;

	@Autowired
	protected SalesRepository salesRepository;

	@Test
	public void testUpdateSales() {
		// We sold 5 more units of stuff
		SalesView sales = salesViewRepository.findByFiscalDateAndStoreNumberAndProductName(LocalDate.of(2020, 1, 6),
				"454", "Stuff");
		assertNotNull(sales);
		salesRepository.saveAndFlush(sales.asUpdatable(sales.getUnitsSold() + 5L));
	}
}
