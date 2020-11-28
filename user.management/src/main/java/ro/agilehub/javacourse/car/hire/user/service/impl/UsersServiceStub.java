package ro.agilehub.javacourse.car.hire.user.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;
import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserStatusDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;
import ro.agilehub.javacourse.car.hire.user.execption.UserAlreadyExistsException;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

@Qualifier("usersServiceStub")
@Service
public class UsersServiceStub implements UsersService {

	private final List<UserResponseDTO> userStubs = new ArrayList<>();

	@PostConstruct
	private void initStubs() {
		for(int i = 1; i <= 50; i++) {
			UserResponseDTO dto = new UserResponseDTO();
			dto.setCountry("RO");
			dto.setDriverLicenseNo("A653BD00" + i);
			dto.setEmail("user_stub" + i + "@yahoo.com");
			dto.setFirstName("User First Stub " + i);
			dto.setId("" + i);
			dto.setLastName("User Last Stub " + i);
			dto.setStatus((i % 10) == 0 ? UserStatusDTO.DELETED
					: UserStatusDTO.ACTIVE);
			dto.setUsername("Username Stub " + i);
			userStubs.add(dto);
		}
	}

	@Override
	public String addUser(UserRequestDTO userDTO) {
		String email = userDTO.getEmail();
		validateEmail(null, email);
		int nextId = userStubs.size() + 1;
		UserResponseDTO user = new UserResponseDTO();
		user.setCountry(userDTO.getCountry());
		user.setDriverLicenseNo(userDTO.getDriverLicenseNo());
		user.setEmail(email);
		user.setFirstName(userDTO.getFirstName());
		user.setId(String.valueOf(nextId));
		user.setLastName(userDTO.getLastName());
		user.setStatus(UserStatusDTO.ACTIVE);
		user.setUsername(userDTO.getUsername());
		userStubs.add(user);
		return user.getId();
	}

	//TODO validare pattern email
	private void validateEmail(String skipId, String email) {
		UserResponseDTO userMatch = userStubs.stream()
				.filter(u -> skipId == null ? true : !u.getId().equals(skipId))
				.filter(u -> u.getEmail()
				.equals(email)).findFirst().orElse(null);
		if(userMatch != null)
			throw new UserAlreadyExistsException(email);
	}

	@Override
	public boolean removeUserById(String id) {
		UserResponseDTO user = getUser(id);
		user.setStatus(UserStatusDTO.DELETED);
		//Should I remove from all list?
		return userStubs.remove(user);
	}

	@Override
	public UserResponseDTO getUser(String id) {
		return userStubs.stream().filter(u -> u.getId().equals(id))
				.findFirst().orElseThrow(() ->
				new NoSuchElementException("No user found with id " + id));
	}

	@Override
	public PageUsers findAll(Integer page, Integer size, String sort) {
		int totalNoUsers = userStubs.size();
		List<List<UserResponseDTO>> listUsers = new ArrayList<>();
		for(int i = 0; i < totalNoUsers; i = i + size) {
			int limit = i + size;
			listUsers.add(userStubs.subList(i, limit > totalNoUsers ?
					totalNoUsers : limit));
		}

		List<UserResponseDTO> finalList = listUsers.size() > page ?
				listUsers.get(page) : Collections.emptyList();

		PageUsers pageUsers = new PageUsers();
		pageUsers.setCurrentPage(page);
		pageUsers.setPageSize(finalList.size());
		pageUsers.setTotalNoRecords(totalNoUsers);
		pageUsers.setTotalPages(listUsers.size());
		pageUsers.setUsers(finalList);
		return pageUsers;
	}

	@Override
	public boolean updateUser(String id, List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");

		UserResponseDTO user = getUser(id);
		for(PatchDocument patch : patchDocuments)
			if(!applyPatch(patch, user))
				return false;
		return true;
	}

	private boolean applyPatch(PatchDocument patch, UserResponseDTO user) {
		String path = patch.getPath();

		Pattern pattern = Pattern.compile("/\\w+");
		Matcher matcher = pattern.matcher(path);
		if(!matcher.matches())
			throw new PatchException("Path is invalid! An expression "
					+ "'/<userAttr>' is accepted.");

		String userAttribute = path.replace("/", "");
		Object value = patch.getValue();

		try {
			if("email".equals(userAttribute))
				validateEmail(user.getId(), (String) value);
			if("id".equals(userAttribute))
				throw new PatchException("Invalid attribute 'id' for update.");
			if("status".equals(userAttribute))
				value = UserStatusDTO.fromValue((String) value);

			Field field = user.getClass().getDeclaredField(userAttribute);
			field.setAccessible(true);
			field.set(user, value);
			return true;
		} catch (NoSuchFieldException e) {
			throw new PatchException("Object User doesn't have field '"
					+ e.getMessage() + "'", e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			throw new PatchException(e.getMessage(), userAttribute);
		}
		return false;
	}

	@Override
	public UserDoc getUserDoc(String userId) {
		return null;
	}
}
