package ro.agilehub.javacourse.car.hire.user.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UsersControllerIntegrationTest {

	private static final String PATH_USERS = "/users";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void addUserOk() throws Exception {
		UserRequestDTO newUser = new UserRequestDTO()
				.email("user@domain.com")
				.country("RO")
				.driverLicenseNo("AB1234")
				.firstName("cip")
				.lastName("buz")
				.password("1234")
				.title("Mr")
				.username("ciprianb");

		MvcResult mvcResult = mvc.perform(post(PATH_USERS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isCreated()).andReturn();

		String headerLocationId = mvcResult.getResponse().getHeader(LOCATION);
		assertNotNull(headerLocationId);
		assertNotEquals("", headerLocationId);
		
		URL url = new URL(headerLocationId);
		String path = url.getPath();

		mvcResult = mvc.perform(get(path))
			.andExpect(status().isOk())
			.andReturn();

		UserResponseDTO savedUser = objectMapper.readValue(mvcResult.getResponse()
				.getContentAsString(), UserResponseDTO.class);
		assertEquals(path.replace(PATH_USERS + "/", ""), savedUser.getId());
		assertEquals(newUser.getCountry(), savedUser.getCountry());
		assertEquals(newUser.getDriverLicenseNo(), savedUser.getDriverLicenseNo());
		assertEquals(newUser.getEmail(), savedUser.getEmail());
		assertEquals(newUser.getFirstName(), savedUser.getFirstName());
		assertEquals(newUser.getLastName(), savedUser.getLastName());
		assertEquals(newUser.getTitle(), savedUser.getTitle());
		assertEquals(newUser.getUsername(), savedUser.getUsername());
	}

	@After
	public void cleanCollections() {
		mongoTemplate.dropCollection(UserDoc.COLLECTION_NAME);
	}
}
