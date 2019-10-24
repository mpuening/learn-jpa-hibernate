package io.github.learnjpahibernate.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test-case-data.sql")
public class ShipRestRepositoryTests {

	@Autowired
	protected MockMvc mockMvc;

	@Test
	public void testPostShipCabin() throws Exception {
		assertNotNull(mockMvc);

		String json = new String(Files.readAllBytes(Paths.get("src/test/resources/new-cabin.json")));

		mockMvc.perform(post("/cabins").content(json)
				// .with(csrf()) // import static SecurityMockMvcRequestPostProcessors.csrf;
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isCreated());

		mockMvc.perform(get("/ships/3/cabins?sort=id")).andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("_embedded.cabins[0]._links.self.href", startsWith("http://localhost/cabins")))
				.andExpect(jsonPath("_embedded.cabins[0].deckLevel", equalTo(1)))
				.andExpect(jsonPath("_embedded.cabins[0].bedCount", equalTo(1)))
				.andExpect(jsonPath("_embedded.cabins[0].price", equalTo("USD 99.99")))
				.andExpect(jsonPath("_embedded.cabins[1]._links.self.href", startsWith("http://localhost/cabins")))
				.andExpect(jsonPath("_embedded.cabins[1].deckLevel", equalTo(1)))
				.andExpect(jsonPath("_embedded.cabins[1].bedCount", equalTo(1)))
				.andExpect(jsonPath("_embedded.cabins[1].price", equalTo("USD 99.99")))
				.andExpect(jsonPath("_embedded.cabins[2]._links.self.href", startsWith("http://localhost/cabins")))
				.andExpect(jsonPath("_embedded.cabins[2].deckLevel", equalTo(101)))
				.andExpect(jsonPath("_embedded.cabins[2].bedCount", equalTo(50)))
				.andExpect(jsonPath("_embedded.cabins[2].price", equalTo("USD 499.95")))
				.andExpect(jsonPath("_embedded.cabins[3].price").doesNotExist());
	}
}
