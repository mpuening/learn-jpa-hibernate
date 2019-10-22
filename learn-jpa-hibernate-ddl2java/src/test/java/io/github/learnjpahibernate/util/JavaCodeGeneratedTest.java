package io.github.learnjpahibernate.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

public class JavaCodeGeneratedTest {
	@Test
	public void testIfJavaCodeGenerated() {
		File bookClass = new File("target/codegen/io/github/learnjpahibernate/Book.java");
		assertTrue(bookClass.exists());

		File authorClass = new File("target/codegen/io/github/learnjpahibernate/Author.java");
		assertTrue(authorClass.exists());

		File preferenceClass = new File("target/codegen/io/github/learnjpahibernate/Preference.java");
		assertTrue(preferenceClass.exists());
	}
}
