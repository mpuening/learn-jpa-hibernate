package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.support.TransactionTemplate;

import io.github.learnjpahibernate.data.EntityValidator;
import io.github.learnjpahibernate.model.Author;
import io.github.learnjpahibernate.model.BadLombokEntity;
import io.github.learnjpahibernate.model.BadSimpleEntity;
import io.github.learnjpahibernate.model.Book;
import io.github.learnjpahibernate.repository.AuthorRepository;
import io.github.learnjpahibernate.repository.BadLombokEntityRepository;
import io.github.learnjpahibernate.repository.BadSimpleEntityRepository;
import io.github.learnjpahibernate.repository.BookRepository;

@DataJpaTest
public class LearnJPAHibernateApplicationTests {

	@Autowired
	protected TransactionTemplate transactionTemplate;

	@Autowired
	protected AuthorRepository authorRepository;

	@Autowired
	protected BookRepository bookRepository;

	@Autowired
	protected BadSimpleEntityRepository badSimpleEntityRepository;

	@Autowired
	protected BadLombokEntityRepository badLombokEntityRepository;

	@Test
	public void validateAuthor() {
		assertNotNull(authorRepository);
		Author author = new Author();
		author.setName("");

		EntityValidator.with(author, authorRepository).beforeUpdate(a -> {
			a.setName("John Doe");
		}).withTransactions(transactionTemplate).assertEntityIsValid();
	}

	@Test
	public void validateBook() {
		assertNotNull(bookRepository);
		Book book = new Book();
		book.setIsbn("978-9730228236");
		book.setName("High-Performance Java Persistence");

		EntityValidator.with(book, bookRepository).beforeUpdate(b -> {
			b.setName("More High-Performance Java Persistence");
		}).withTransactions(transactionTemplate).assertEntityIsValid();
	}

	@Test
	public void validateBadSimpleEntity() {
		Assertions.assertThrows(RuntimeException.class, () -> {
			assertNotNull(badSimpleEntityRepository);
			BadSimpleEntity badSimpleEntity = new BadSimpleEntity();
			badSimpleEntity.setName("simple");

			EntityValidator.with(badSimpleEntity, badSimpleEntityRepository).beforeUpdate(b -> {
				b.setName("bad simple");
			}).withTransactions(transactionTemplate).assertEntityIsValid();
		});
	}

	@Test
	public void validateBadLombokEntity() {
		assertNotNull(badLombokEntityRepository);
		BadLombokEntity badLombokEntity = new BadLombokEntity();
		badLombokEntity.setIsbn("Bad");
		badLombokEntity.setName("lombok");

		Assertions.assertThrows(RuntimeException.class, () -> {
			EntityValidator.with(badLombokEntity, badLombokEntityRepository).beforeUpdate(b -> {
				b.setName("bad lombok");
			}).withTransactions(transactionTemplate).assertEntityIsValid();
		});
	}
}
