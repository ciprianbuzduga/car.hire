package ro.agilehub.javacourse.car.hire.user.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ro.agilehub.javacourse.car.hire.api.exception.DuplicateKeyErrorCollection;
import ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException;
import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;
import ro.agilehub.javacourse.car.hire.user.mapper.UserMapper;
import ro.agilehub.javacourse.car.hire.user.repository.UserRepository;
import ro.agilehub.javacourse.car.hire.user.service.impl.UsersServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UsersServiceTest {

	private static final String MOCK_ID = "1234";

	@InjectMocks
	private UsersServiceImpl usersService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Test
	public void test_addUser_whenFindByEmail_thenException() {
		when(userRepository.countByEmail(any())).thenReturn(1L);
		
		UserRequestDTO newUser = mock(UserRequestDTO.class);
		
		EntityAlreadyExistsException exception = assertThrows(
				EntityAlreadyExistsException.class,
				() -> usersService.addUser(newUser));
		assertEquals("email", exception.getField());
	}

	@Test
	public void test_addUser_whenFindByUsername_thenException() {
		when(userRepository.countByEmail(any())).thenReturn(0L);
		when(userRepository.countByUsername(any())).thenReturn(1L);
		
		UserRequestDTO newUser = mock(UserRequestDTO.class);
		
		EntityAlreadyExistsException exception = assertThrows(
				EntityAlreadyExistsException.class,
				() -> usersService.addUser(newUser));
		assertEquals("username", exception.getField());
	}

	@Test
	public void test_addUser_whenAllOK() {
		when(userRepository.countByEmail(any())).thenReturn(0L);
		when(userRepository.countByUsername(any())).thenReturn(0L);
		
		UserRequestDTO newUser = mock(UserRequestDTO.class);
		UserDoc userDoc = mock(UserDoc.class);
		
		when(userMapper.mapToUserDoc(newUser)).thenReturn(userDoc);
		when(userRepository.save(userDoc)).thenReturn(userDoc);
		when(userDoc.get_id()).thenReturn(MOCK_ID);
		
		String newId = usersService.addUser(newUser);
		assertNotNull(newId);
		assertEquals(userDoc.get_id(), newId);
	}

	@Test
	public void test_addUser_whenConcurrentAccessException() {
		when(userRepository.countByEmail(any())).thenReturn(0L);
		when(userRepository.countByUsername(any())).thenReturn(0L);
		
		UserRequestDTO newUser = mock(UserRequestDTO.class);
		UserDoc userDoc = mock(UserDoc.class);
		DuplicateKeyException duplicateKeyException = mock(DuplicateKeyException.class);
		RuntimeException causeException = mock(RuntimeException.class);
		
		when(userMapper.mapToUserDoc(newUser)).thenReturn(userDoc);
		when(duplicateKeyException.getCause()).thenReturn(causeException);
		when(causeException.getMessage()).thenReturn(any());
		when(userRepository.save(userDoc)).thenThrow(duplicateKeyException);
		
		assertThrows(DuplicateKeyErrorCollection.class,
				() -> usersService.addUser(newUser));
	}
	
	@Test
	public void test_addUser_whenUnknownException() {
		when(userRepository.countByEmail(any())).thenReturn(0L);
		when(userRepository.countByUsername(any())).thenReturn(0L);
		
		UserRequestDTO newUser = mock(UserRequestDTO.class);
		UserDoc userDoc = mock(UserDoc.class);
		RuntimeException unknownException = mock(RuntimeException.class);
		
		when(userMapper.mapToUserDoc(newUser)).thenReturn(userDoc);
		when(userRepository.save(userDoc)).thenThrow(unknownException);
		
		String newId = usersService.addUser(newUser);
		assertNull(newId);
	}

	@Test
	public void test_removeUserById_whenNotFound() {
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> usersService.removeUserById(MOCK_ID));
	}

	@Test
	public void test_removeUserById_whenFound() {
		UserDoc userDoc = mock(UserDoc.class);
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.of(userDoc));
		doNothing().when(userRepository).delete(userDoc);
		
		assertTrue(usersService.removeUserById(MOCK_ID));
	}
	
	@Test
	public void test_removeUserById_whenUnknownException() {
		UserDoc userDoc = mock(UserDoc.class);
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.of(userDoc));
		RuntimeException unknownException = mock(RuntimeException.class);
		doThrow(unknownException).when(userRepository).delete(userDoc);
		
		assertFalse(usersService.removeUserById(MOCK_ID));
	}
	
	@Test
	public void test_getUserDoc_whenFound() {
		UserDoc userMock = mock(UserDoc.class);
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.of(userMock));
		
		UserDoc userFound = usersService.getUserDoc(MOCK_ID);
		assertNotNull(userFound);
		assertEquals(userMock, userFound);
	}
	
	@Test
	public void test_getUserDoc_whenNotFound() {
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> usersService.getUserDoc(MOCK_ID));
	}
	
	@Test
	public void test_getUser_whenFound() {
		UserDoc userMock = mock(UserDoc.class);
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.of(userMock));
		
		UserResponseDTO mockUserResponse = mock(UserResponseDTO.class);
		when(userMapper.mapToUserResponseDTO(userMock)).thenReturn(mockUserResponse);
		
		UserResponseDTO userResponse = usersService.getUser(MOCK_ID);
		assertNotNull(userResponse);
		assertEquals(mockUserResponse, userResponse);
	}
	
	@Test
	public void test_getUser_whenNotFound() {
		when(userRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> usersService.getUser(MOCK_ID));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_findAll_whenFound_firstPage() {
		Page<UserDoc> mockPageUsers = mock(Page.class);
		int page = 0;
		int size = 10;
		long totalUsersSize = 50;
		int totalPages = (int) totalUsersSize / size;
		List<UserDoc> listUsers = new ArrayList<>(size);
		for(int i = 0; i < size; i++)
			listUsers.add(mock(UserDoc.class));
		
		when(mockPageUsers.iterator()).thenReturn(listUsers.iterator());
		when(mockPageUsers.getNumberOfElements()).thenReturn(size);
		when(mockPageUsers.getTotalElements()).thenReturn(totalUsersSize);
		when(mockPageUsers.getTotalPages()).thenReturn(totalPages);
		when(mockPageUsers.hasContent()).thenReturn(true);
		
		when(userRepository.findAll(any(Pageable.class))).thenReturn(mockPageUsers);
		when(userMapper.mapToUserResponseDTO(any(UserDoc.class)))
			.thenReturn(mock(UserResponseDTO.class));
		
		doCallRealMethod().when(mockPageUsers).forEach(any(Consumer.class));
		
		PageUsers pageUsers = usersService.findAll(page, size, null);
		assertNotNull(pageUsers);
		assertEquals(size, pageUsers.getUsers().size());
		
		assertEquals(page, pageUsers.getCurrentPage());
		assertEquals(size, pageUsers.getPageSize());
		assertEquals((int) totalUsersSize, pageUsers.getTotalNoRecords());
		assertEquals(totalPages, pageUsers.getTotalPages());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_findAll_whenNotFound() {
		Page<UserDoc> mockPageUsers = mock(Page.class);
		when(mockPageUsers.getNumberOfElements()).thenReturn(0);
		when(mockPageUsers.getTotalElements()).thenReturn(0l);
		when(mockPageUsers.getTotalPages()).thenReturn(0);
		when(mockPageUsers.hasContent()).thenReturn(false);
		
		when(userRepository.findAll(any(Pageable.class))).thenReturn(mockPageUsers);
		
		PageUsers pageUsers = usersService.findAll(0, 10, null);
		assertNotNull(pageUsers);
		assertNull(pageUsers.getUsers());
		assertEquals(0, pageUsers.getCurrentPage());
		assertEquals(0, pageUsers.getPageSize());
		assertEquals(0, pageUsers.getTotalNoRecords());
		assertEquals(0, pageUsers.getTotalPages());
	}
	
	//TODO Patch user Test
}
