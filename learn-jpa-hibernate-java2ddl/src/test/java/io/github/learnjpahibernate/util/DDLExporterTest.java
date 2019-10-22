package io.github.learnjpahibernate.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

public class DDLExporterTest {
	@Test
	public void exportDDL() throws Exception {
		DDLExporter exporter = new DDLExporter();
		exporter.exportDDL("io.github.learnjpahibernate.model", "org.hibernate.dialect.H2Dialect",
				"target/schema-generated.sql");

		File actual = new File("target/schema-generated.sql");
		File expected = new File("src/test/resources/schema-expected.sql");
		assertTrue(FileUtils.contentEquals(actual, expected), "The schemas differ!");
	}
}
