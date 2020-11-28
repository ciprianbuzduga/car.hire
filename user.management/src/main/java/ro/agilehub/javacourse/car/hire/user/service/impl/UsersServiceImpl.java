package ro.agilehub.javacourse.car.hire.user.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;
import ro.agilehub.javacourse.car.hire.user.mapper.UserMapper;
import ro.agilehub.javacourse.car.hire.user.repository.UserRepository;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

@Qualifier("usersService")
@Service
public class UsersServiceImpl implements UsersService {

	private final UserRepository repository;
	private final UserMapper userMapper;

	public UsersServiceImpl(UserRepository repository,
			UserMapper userMapper) {
		this.repository = repository;
		this.userMapper = userMapper;
	}

	@Override
	public String addUser(UserRequestDTO userDTO) {
		try {
			UserDoc user = userMapper.mapToUserDoc(userDTO);
			user = repository.save(user);
			return user.get_id();
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
		PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, UserDoc.class);
		return repository.updateDoc(UserDoc.class, id, patchMapper.getFieldValues());
	}

}
