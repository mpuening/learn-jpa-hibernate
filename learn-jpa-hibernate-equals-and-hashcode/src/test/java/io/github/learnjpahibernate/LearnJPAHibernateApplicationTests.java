package io.github.learnjpahibernate;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.learnjpahibernate.data.EntityValidator;
import io.github.learnjpahibernate.model.Author;
import io.github.learnjpahibernate.model.BadLombokEntity;
import io.github.learnjpahibernate.model.BadSimpleEntity;
import io.github.learnjpahibernate.model.Book;
import io.github.learnjpahibernate.repository.AuthorRepository;
import io.github.learnjpahibernate.repository.BadLombokEntityRepository;
import io.github.learnjpahibernate.repository.BadSimpleEntityRepository;
import io.github.learnjpahibernate.repository.BookRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LearnJPAHibernateApplicationTests {

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

		EntityValidator.assertEntityIsValid(author, authorRepository, a -> {
			a.setName("John Doe");
		});
	}

	@Test
	public void validateBook() {
		assertNotNull(bookRepository);
		Book book = new Book();
		book.setIsbn("978-9730228236");
		book.setName("High-Performance Java Persistence");

		EntityValidator.assertEntityIsValid(book, bookRepository, b -> {
			b.setName("More High-Performance Java Persistence");
		});
	}

	@Test(expected = RuntimeException.class)
	public void validateBadSimpleEntity() {
		assertNotNull(badSimpleEntityRepository);
		BadSimpleEntity badSimpleEntity = new BadSimpleEntity();
		badSimpleEntity.setName("simple");

		EntityValidator.assertEntityIsValid(badSimpleEntity, badSimpleEntityRepository, b -> {
			b.setName("bad simple");
		});
	}

	@Test(expected = RuntimeException.class)
	public void validateBadLombokEntity() {
		assertNotNull(badLombokEntityRepository);
		BadLombokEntity badLombokEntity = new BadLombokEntity();
		badLombokEntity.setIsbn("Bad");
		badLombokEntity.setName("lombok");

		EntityValidator.assertEntityIsValid(badLombokEntity, badLombokEntityRepository, a -> {
			a.setName("bad lombok");
		});
	}
}
