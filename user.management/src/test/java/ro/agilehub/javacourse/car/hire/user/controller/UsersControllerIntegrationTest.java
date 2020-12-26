package ro.agilehub.javacourse.car.hire.user.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException.ENTITY_ALREADY_EXISTS_CODE;

import java.net.URL;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.ValidationDTO;
import ro.agilehub.javacourse.car.hire.user.MockMvcSetup;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

@WithMockUser(roles = "MANAGER")
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("integrationtest")
public class UsersControllerIntegrationTest extends MockMvcSetup {

	private static final String PATH_USERS = "/users";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void test_addUserOk() throws Exception {
		UserRequestDTO newUser = createUserRequestDTO();

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

	@Test
	public void test_checkDuplicateUserByEmail() throws Exception {
		UserRequestDTO newUser = createUserRequestDTO();
		newUser.setUsername("ciprianb1");
		
		MvcResult mvcResult = mvc.perform(post(PATH_USERS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isCreated()).andReturn();
		
		newUser.setUsername("ciprianb2");
		mvcResult = mvc.perform(post(PATH_USERS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isBadRequest()).andReturn();
		
		ValidationDTO response = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				ValidationDTO.class);
		
		assertEquals(ENTITY_ALREADY_EXISTS_CODE, response.getCode());
		assertEquals("email", response.getField());
	}

	@Test
	public void test_checkDuplicateUserByUsername() throws Exception {
		UserRequestDTO newUser = createUserRequestDTO();
		newUser.setEmail("user1@domain.com");
		
		MvcResult mvcResult = mvc.perform(post(PATH_USERS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isCreated()).andReturn();
		
		newUser.setEmail("user2@domain.com");
		mvcResult = mvc.perform(post(PATH_USERS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isBadRequest()).andReturn();
		
		ValidationDTO response = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				ValidationDTO.class);
		
		assertEquals(ENTITY_ALREADY_EXISTS_CODE, response.getCode());
		assertEquals("username", response.getField());
	}

	@Test
	public void test_removeNotFoundUser() throws Exception {
		String transientId = "1";
		mvc.perform(delete(PATH_USERS + "/" + transientId))
				.andExpect(status().isNotFound())
				.andReturn();
	}
	
	@Test
	public void test_remove_FoundUser() throws Exception {
		UserRequestDTO newUser = createUserRequestDTO();

		MvcResult mvcResult = mvc.perform(post(PATH_USERS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isCreated()).andReturn();

		String headerLocationId = mvcResult.getResponse().getHeader(LOCATION);
		URL url = new URL(headerLocationId);
		String path = url.getPath();

		mvcResult = mvc.perform(delete(path))
			.andExpect(status().isNoContent())
			.andReturn();
		
		mvcResult = mvc.perform(get(path))
				.andExpect(status().isNotFound())
				.andReturn();
	}

	private UserRequestDTO createUserRequestDTO() {
		UserRequestDTO newUser = new UserRequestDTO()
				.email("user@domain.com")
				.country("RO")
				.driverLicenseNo("AB1234")
				.firstName("cip")
				.lastName("buz")
				.password("1234")
				.title("Mr")
				.username("ciprianb");
		return newUser;
	}

	@After
	public void cleanCollections() {
		mongoTemplate.dropCollection(UserDoc.COLLECTION_NAME);
	}
}
