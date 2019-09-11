package io.github.learnjpahibernate.util;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DDLExporterTest {
	@Test
	public void exportDDL() throws Exception {
		DDLExporter exporter = new DDLExporter();
		exporter.exportDDL("io.github.learnjpahibernate.model", "org.hibernate.dialect.H2Dialect",
				"target/schema-generated.sql");

		File actual = new File("target/schema-generated.sql");
		File expected = new File("src/test/resources/schema-expected.sql");
		assertTrue("The schemas differ!", FileUtils.contentEquals(actual, expected));
	}
}
