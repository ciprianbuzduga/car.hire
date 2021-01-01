package ro.agilehub.javacourse.car.hire.fleet.controller;

import static org.junit.Assert.assertFalse;
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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import ro.agilehub.javacourse.car.hire.api.model.CarClazzCodeDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarStatusDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.ValidationDTO;
import ro.agilehub.javacourse.car.hire.base.test.MockMvcIntegrationMongoSetup;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("integrationtest")
public class CarsControllerIntegrationTest extends MockMvcIntegrationMongoSetup {

	@Override
	protected String[] getDroppedCollections() {
		return new String[]{ CarDoc.COLLECTION_NAME };
	}

	@Test
	public void test_addCar_and_getCar() throws Exception {
		CarRequestDTO newCar = createCarRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_CARS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCar))
				.with(ADMIN))
				.andExpect(status().isCreated()).andReturn();
		
		String headerLocationId = mvcResult.getResponse().getHeader(LOCATION);
		assertNotNull(headerLocationId);
		assertNotEquals("", headerLocationId);
		
		String path = getPath(headerLocationId);

		mvcResult = mvc.perform(get(path).with(getRandomRole()))
			.andExpect(status().isOk())
			.andReturn();

		CarResponseDTO savedCar = objectMapper.readValue(mvcResult.getResponse()
				.getContentAsString(), CarResponseDTO.class);
		assertEquals(path.replace(PATH_CARS + "/", ""), savedCar.getId());
		assertCars(newCar, savedCar);
	}

	@Test
	public void test_checkDuplicateByRegistrationNo() throws Exception {
		CarRequestDTO newCar = createCarRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_CARS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCar))
				.with(ADMIN))
				.andExpect(status().isCreated()).andReturn();
		
		mvcResult = mvc.perform(post(PATH_CARS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCar))
				.with(ADMIN))
				.andExpect(status().isBadRequest()).andReturn();
		
		ValidationDTO response = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				ValidationDTO.class);
		
		assertEquals(ENTITY_ALREADY_EXISTS_CODE, response.getCode());
		assertEquals("registrationNo", response.getField());
	}
	
	@Test
	public void test_deleteCar_whenFound() throws Exception {
		CarRequestDTO newCar = createCarRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_CARS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCar))
				.with(ADMIN))
				.andExpect(status().isCreated()).andReturn();
		
		String headerLocationId = mvcResult.getResponse().getHeader(LOCATION);
		String path = getPath(headerLocationId);

		mvcResult = mvc.perform(delete(path).with(ADMIN))
			.andExpect(status().isNoContent())
			.andReturn();
		
		mvcResult = mvc.perform(get(path).with(getRandomRole()))
				.andExpect(status().isNotFound())
				.andReturn();
	}

	@WithMockUser(authorities = "ADMIN")
	@Test
	public void test_deleteCar_whenNotFound() throws Exception {
		String transientId = "1";
		mvc.perform(delete(PATH_CARS + "/" + transientId))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void test_getCars() throws Exception {
		CarRequestDTO newCar = createCarRequestDTO();
		
		MvcResult mvcResult = mvc.perform(post(PATH_CARS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCar))
				.with(ADMIN))
				.andExpect(status().isCreated()).andReturn();
		
		mvcResult = mvc.perform(get(PATH_CARS)
				.queryParam("status", CarStatusDTO.ACTIVE.name())
				.with(getRandomRole()))
				.andExpect(status().isOk())
				.andReturn();
		
		PageCars response = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				PageCars.class);
		assertNotNull(response);
		List<CarResponseDTO> cars = response.getCars();
		assertNotNull(cars);
		assertFalse(cars.isEmpty());
		assertCars(newCar, cars.get(0));
	}

	private CarRequestDTO createCarRequestDTO() {
		CarRequestDTO newCar = new CarRequestDTO()
				.clazzCode(CarClazzCodeDTO.COMPACT_CAR)
				.fuel("gasoline").make("BMW")
				.mileage(1000).model("M4")
				.registrationNo("`1234ABB").year(2020);
		return newCar;
	}
	
	private void assertCars(CarRequestDTO newCar, CarResponseDTO savedCar) {
		assertEquals(newCar.getClazzCode(), savedCar.getClazzCode());
		assertEquals(newCar.getFuel(), savedCar.getFuel());
		assertEquals(newCar.getMake(), savedCar.getMake());
		assertEquals(newCar.getMileage(), savedCar.getMileage());
		assertEquals(newCar.getModel(), savedCar.getModel());
		assertEquals(newCar.getRegistrationNo(), savedCar.getRegistrationNo());
		assertEquals(newCar.getYear(), savedCar.getYear());
		assertEquals(CarStatusDTO.ACTIVE, savedCar.getStatus());
	}

}
