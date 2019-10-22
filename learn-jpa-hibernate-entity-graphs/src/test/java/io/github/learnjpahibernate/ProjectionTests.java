package io.github.learnjpahibernate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectionTests {

	@Autowired
	protected MockMvc mockMvc;

	@Test
	public void testGetCoursesPoorly() throws Exception {
		assertNotNull(mockMvc);
		mockMvc.perform(get("/api/courses/poorly")).andDo(print()).andExpect(status().is5xxServerError());
	}

	@Test
	public void testGetCoursesWithCompactProjection() throws Exception {
		assertNotNull(mockMvc);

		mockMvc.perform(get("/api/courses")).andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].name").exists())
				.andExpect(jsonPath("$[0].teacher").doesNotExist())
				.andExpect(jsonPath("$[0].students").doesNotExist())
				// Test end of the list
				.andExpect(jsonPath("$[15].id").doesNotExist());
	}

	@Test
	public void testGetCoursesWithTeacherProjection() throws Exception {
		assertNotNull(mockMvc);

		mockMvc.perform(get("/api/courses?projection=teacher")).andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].name").exists())
				.andExpect(jsonPath("$[0].teacher").exists())
				.andExpect(jsonPath("$[0].teacher.id").exists())
				.andExpect(jsonPath("$[0].teacher.name").exists())
				.andExpect(jsonPath("$[0].teacher.courses").doesNotExist())
				.andExpect(jsonPath("$[0].students").doesNotExist())
				// Test end of the list
				.andExpect(jsonPath("$[15].id").doesNotExist());
	}

	@Test
	public void testGetCoursesWithStudentProjection() throws Exception {
		assertNotNull(mockMvc);

		mockMvc.perform(get("/api/courses?projection=student")).andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].name").exists())
				.andExpect(jsonPath("$[0].students").exists())
				.andExpect(jsonPath("$[0].students[0].id").exists())
				.andExpect(jsonPath("$[0].students[0].name").exists())
				.andExpect(jsonPath("$[0].students[0].courses").doesNotExist())
				.andExpect(jsonPath("$[0].teacher").doesNotExist())
				// Test end of the list
				.andExpect(jsonPath("$[15].id").doesNotExist());
	}

	@Test
	public void testGetCoursesWithFullProjection() throws Exception {
		assertNotNull(mockMvc);

		mockMvc.perform(get("/api/courses?projection=full")).andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].name").exists())
				.andExpect(jsonPath("$[0].teacher").exists())
				.andExpect(jsonPath("$[0].teacher.id").exists())
				.andExpect(jsonPath("$[0].teacher.name").exists())
				.andExpect(jsonPath("$[0].teacher.courses").doesNotExist())
				.andExpect(jsonPath("$[0].students").exists())
				.andExpect(jsonPath("$[0].students[0].id").exists())
				.andExpect(jsonPath("$[0].students[0].name").exists())
				.andExpect(jsonPath("$[0].students[0].courses").doesNotExist())
				// Test end of the list
				.andExpect(jsonPath("$[15].id").doesNotExist());
	}
}
