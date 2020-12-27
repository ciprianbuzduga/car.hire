package ro.agilehub.javacourse.car.hire.user.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException.ENTITY_ALREADY_EXISTS_CODE;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.type.CollectionType;

import ro.agilehub.javacourse.car.hire.api.model.CountryRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryStatusDTO;
import ro.agilehub.javacourse.car.hire.api.model.ValidationDTO;
import ro.agilehub.javacourse.car.hire.user.MockMvcIntegrationMongoSetup;
import ro.agilehub.javacourse.car.hire.user.document.CountryDoc;

@WithMockUser(roles = "ADMIN")
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("integrationtest")
public class CountriesControllerIntegrationTest extends MockMvcIntegrationMongoSetup {
	private static final String PATH_COUNTRIES = "/countries";
	
	@Test
	public void test_createCountry_OK() throws Exception {
		CountryRequestDTO newCountry = createCountryRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isCreated()).andReturn();
		
		String headerLocationId = mvcResult.getResponse().getHeader(LOCATION);
		assertNotNull(headerLocationId);
		assertNotEquals("", headerLocationId);
		
		String path = getPath(headerLocationId);

		mvcResult = mvc.perform(get(path))
			.andExpect(status().isOk())
			.andReturn();
		
		CountryResponseDTO savedCountry = objectMapper.readValue(mvcResult.getResponse()
				.getContentAsString(), CountryResponseDTO.class);
		assertEquals(path.replace(PATH_COUNTRIES + "/", ""), savedCountry.getId());
		assertEquals(newCountry.getIsoCode(), savedCountry.getIsoCode());
		assertEquals(newCountry.getName(), savedCountry.getName());
		assertEquals(CountryStatusDTO.ACTIVE, savedCountry.getStatus());
	}

	private CountryRequestDTO createCountryRequestDTO() {
		CountryRequestDTO newCountry = new CountryRequestDTO();
		newCountry.isoCode("RO").name("Romania");
		return newCountry;
	}
	
	@Test
	public void test_checkDuplicateCountryByIsocode() throws Exception {
		CountryRequestDTO newCountry = createCountryRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isCreated()).andReturn();
		
		newCountry.setName("Italy");
		mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isBadRequest()).andReturn();
		
		ValidationDTO response = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				ValidationDTO.class);
		
		assertEquals(ENTITY_ALREADY_EXISTS_CODE, response.getCode());
		assertEquals("isoCode", response.getField());
	}
	
	@Test
	public void test_checkDuplicateCountryByName() throws Exception {
		CountryRequestDTO newCountry = createCountryRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isCreated()).andReturn();
		
		newCountry.setIsoCode("IT");
		mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isBadRequest()).andReturn();
		
		ValidationDTO response = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				ValidationDTO.class);
		
		assertEquals(ENTITY_ALREADY_EXISTS_CODE, response.getCode());
		assertEquals("name", response.getField());
	}
	
	@Test
	public void test_delete_NotFoundCountry() throws Exception {
		String transientId = "1";
		mvc.perform(delete(PATH_COUNTRIES + "/" + transientId))
				.andExpect(status().isNotFound())
				.andReturn();
	}

	@Test
	public void test_delete_FoundCountry() throws Exception {
		CountryRequestDTO newCountry = createCountryRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isCreated()).andReturn();
		
		String headerLocationId = mvcResult.getResponse().getHeader(LOCATION);
		String path = getPath(headerLocationId);

		mvcResult = mvc.perform(delete(path))
			.andExpect(status().isNoContent())
			.andReturn();
		
		mvcResult = mvc.perform(get(path))
				.andExpect(status().isNotFound())
				.andReturn();
	}
	
	@Test
	public void test_findAll_whenFound() throws Exception {
		CountryRequestDTO newCountry = createCountryRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_COUNTRIES)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry)))
				.andExpect(status().isCreated()).andReturn();
		
		mvcResult = mvc.perform(get(PATH_COUNTRIES))
				.andExpect(status().isOk())
				.andReturn();
		
		CollectionType javaType = objectMapper.getTypeFactory()
			      .constructCollectionType(List.class, CountryResponseDTO.class);
		List<CountryResponseDTO> savedCountries = objectMapper
				.readValue(mvcResult.getResponse()
				.getContentAsString(), javaType);
		
		assertEquals(1, savedCountries.size());
		
		CountryResponseDTO savedCountry = savedCountries.get(0);
		assertNotNull(savedCountry.getId());
		assertEquals(newCountry.getIsoCode(), savedCountry.getIsoCode());
		assertEquals(newCountry.getName(), savedCountry.getName());
		assertEquals(CountryStatusDTO.ACTIVE, savedCountry.getStatus());
	}
	
	@Test
	public void test_findAll_whenNotFound() throws Exception {
		MvcResult mvcResult = mvc.perform(get(PATH_COUNTRIES))
				.andExpect(status().isOk())
				.andReturn();
		
		CollectionType javaType = objectMapper.getTypeFactory()
			      .constructCollectionType(List.class, CountryResponseDTO.class);
		List<CountryResponseDTO> savedCountries = objectMapper
				.readValue(mvcResult.getResponse()
				.getContentAsString(), javaType);
		
		assertTrue(savedCountries.isEmpty());
	}

	@Override
	protected String[] getDroppedCollections() {
		return new String[]{ CountryDoc.COLLECTION_NAME };
	}
}
