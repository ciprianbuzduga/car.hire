package ro.agilehub.javacourse.car.hire.user.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.exception.DuplicateKeyErrorCollection;
import ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException;
import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;
import ro.agilehub.javacourse.car.hire.user.mapper.UserMapper;
import ro.agilehub.javacourse.car.hire.user.repository.UserRepository;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

	private final UserRepository repository;
	private final UserMapper userMapper;

	@Override
	public String addUser(UserRequestDTO userDTO) {
		String email = userDTO.getEmail();
		long countUsers = repository.countByEmail(email);
		if(countUsers > 0)
			throw new EntityAlreadyExistsException(UserDoc.COLLECTION_NAME,
					"email", email);

		String username = userDTO.getUsername();
		countUsers = repository.countByUsername(username);
		if(countUsers > 0)
			throw new EntityAlreadyExistsException(UserDoc.COLLECTION_NAME,
					"username", username);

		try {
			UserDoc user = userMapper.mapToUserDoc(userDTO);
			user = repository.save(user);
			return user.get_id();
		} catch (DuplicateKeyException e) {
			String message = e.getCause().getMessage();
			System.err.println("Unique index constraint violation: " + message);
			throw new DuplicateKeyErrorCollection(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean removeUserById(String id) {
		UserDoc user = getUserDoc(id);
		try {
			repository.delete(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public UserDoc getUserDoc(String id) {
		return repository.findById(id).orElseThrow(
				() -> new NoSuchElementException("No user found with id " + id));
	}

	@Override
	public UserResponseDTO getUser(String id) {
		UserDoc user = getUserDoc(id);
		return userMapper.mapToUserResponseDTO(user);
	}

	@Override
	public PageUsers findAll(Integer page, Integer size, String sort) {
		Pageable pageable = PageRequest.of(page, size);
		Page<UserDoc> pageUsersDoc = repository.findAll(pageable);
		PageUsers pageUsers = new PageUsers();
		pageUsers.currentPage(page)
			.pageSize(pageUsersDoc.getNumberOfElements())
			.totalNoRecords((int) pageUsersDoc.getTotalElements())
			.totalPages(pageUsersDoc.getTotalPages());
		if(pageUsersDoc.hasContent())
			pageUsersDoc.forEach(user -> pageUsers.addUsersItem(
					userMapper.mapToUserResponseDTO(user)));
		return pageUsers;
	}

	@Override
	public boolean updateUser(String id, List<PatchDocument> patchDocuments) {
		//TODO validations: email, username
		PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, UserDoc.class);
		boolean result = false;
		try {
			result = repository.updateDoc(UserDoc.class, id, patchMapper.getFieldValues());
		} catch (DuplicateKeyException e) {
			String message = e.getCause().getMessage();
			System.err.println("Unique index constraint violation: " + message);
			throw new DuplicateKeyErrorCollection(message);
		}
		return result;
	}

}
