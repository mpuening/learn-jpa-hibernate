package io.github.learnjpahibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.learnjpahibernate.model.Author;
import io.github.learnjpahibernate.model.Book;
import io.github.learnjpahibernate.model.Preference;
import io.github.learnjpahibernate.repository.BookRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected BookRepository bookRepository;

	@Test
	@Sql("/test-case-data.sql")
	@SuppressWarnings("serial")
	public void contextLoads() {
		assertNotNull(bookRepository);
		Book gofBook = bookRepository.findById("0-201-63361-2")
				.orElseThrow(() -> new IllegalArgumentException("Book not found"));
		assertNotNull(gofBook);
		assertEquals("0-201-63361-2", gofBook.getIsbn());
		assertTrue(gofBook.getName().startsWith("Design Patterns"));

		Set<Author> authors = gofBook.getAuthors();
		assertNotNull(authors);
		assertEquals(4, authors.size());
		final Set<String> authorNames = new HashSet<String>() {
			{
				add("Erich Gamma");
				add("Richard Helm");
				add("Ralph Johnson");
				add("John Vlissides");
			}
		};
		authors.stream().forEach(author -> {
			assertTrue(authorNames.contains(author.getName()));
		});
		Author gamma = authors.stream().filter(author -> author.getName().contains("Gamma")).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Gamma not found"));
		assertNotNull(gamma);

		Set<Book> gammaBooks = gamma.getBooks();
		assertNotNull(gammaBooks);
		assertEquals(2, gammaBooks.size());
		gammaBooks.stream().forEach(book -> {
			assertTrue(book.getName().contains("Patterns"));
		});

		Book eclipseBook = gammaBooks.stream().filter(book -> book.getName().contains("Eclipse")).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Eclipse book not found"));
		assertNotNull(eclipseBook);

		Author beck = eclipseBook.getAuthors().stream().filter(author -> author.getName().contains("Beck")).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Beck not found"));
		assertNotNull(beck);
		
		Set<Preference> preferences = beck.getPreferences();
		assertNotNull(preferences);
		assertEquals(2, preferences.size());
		preferences.stream().forEach(preference -> {
			assertTrue(preference.getId().getAuthorId() == beck.getId());
			assertTrue(preference.getId().getName().length() > 2);
			assertTrue(preference.getValue().length() > 2);
			assertTrue(preference.getAuthor() == beck);
		});
	}

}
