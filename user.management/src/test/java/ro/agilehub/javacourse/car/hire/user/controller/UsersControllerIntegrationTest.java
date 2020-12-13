package ro.agilehub.javacourse.car.hire.user.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;

@RunWith(SpringRunner.class)
@DataMongoTest
//@WebAppConfiguration
@AutoConfigureMockMvc
public class UsersControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

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

		MvcResult mvcResult = mvc.perform(post("/users")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isCreated()).andReturn();

		String headerLocationId = mvcResult.getResponse().getHeader("Location");
		assertNotNull(headerLocationId);
		assertNotEquals("", headerLocationId);

		mvcResult = mvc.perform(get("/user/" + headerLocationId))
			.andExpect(status().isOk())
			.andReturn();

		UserResponseDTO savedUser = objectMapper.readValue(mvcResult.getResponse()
				.getContentAsString(), UserResponseDTO.class);
		assertEquals(headerLocationId, savedUser.getId());
		assertEquals(newUser.getCountry(), savedUser.getCountry());
		assertEquals(newUser.getDriverLicenseNo(), savedUser.getDriverLicenseNo());
		assertEquals(newUser.getEmail(), savedUser.getEmail());
		assertEquals(newUser.getFirstName(), savedUser.getFirstName());
		assertEquals(newUser.getLastName(), savedUser.getLastName());
		assertEquals(newUser.getTitle(), savedUser.getTitle());
		assertEquals(newUser.getUsername(), savedUser.getUsername());
	}
}
