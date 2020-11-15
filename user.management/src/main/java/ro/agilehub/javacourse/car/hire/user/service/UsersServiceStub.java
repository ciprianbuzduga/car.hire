package ro.agilehub.javacourse.car.hire.user.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.UserDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserDTO.StatusEnum;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;
import ro.agilehub.javacourse.car.hire.user.execption.UserAlreadyExistsException;

@Service
public class UsersServiceStub implements UsersService {

	private final List<UserDTO> userStubs = new ArrayList<>();

	@PostConstruct
	private void initStubs() {
		for(int i = 1; i <= 50; i++) {
			UserDTO dto = new UserDTO();
			dto.setCountry("RO");
			dto.setDriverLicenseNo("A653BD00" + i);
			dto.setEmail("user_stub" + i + "@yahoo.com");
			dto.setFirstName("User First Stub " + i);
			dto.setId(i);
			dto.setLastName("User Last Stub " + i);
			dto.setPassword(null);
			dto.setStatus((i % 10) == 0 ? UserDTO.StatusEnum.DELETED : UserDTO.StatusEnum.ACTIVE);
			dto.setUsername("Username Stub " + i);
			userStubs.add(dto);
		}
	}

	@Override
	public boolean addUser(UserDTO userDTO) {
		String email = userDTO.getEmail();
		validateEmail(null, email);
		int nextId = userStubs.size() + 1;
		userDTO.setId(nextId);
		return userStubs.add(userDTO);
	}

	//TODO validare pattern email
	private void validateEmail(Integer skipId, String email) {
		UserDTO userMatch = userStubs.stream()
				.filter(u -> skipId == null ? true : !u.getId().equals(skipId))
				.filter(u -> u.getEmail()
				.equals(email)).findFirst().orElse(null);
		if(userMatch != null)
			throw new UserAlreadyExistsException(email);
	}

	@Override
	public boolean removeUserById(Integer id) {
		UserDTO user = getUser(id);
		user.setStatus(StatusEnum.DELETED);
		//Should I remove from all list?
		return userStubs.remove(user);
	}

	@Override
	public UserDTO getUser(Integer id) {
		return userStubs.stream().filter(u -> u.getId().equals(id))
				.findFirst().orElseThrow(() ->
				new NoSuchElementException("No user found with id " + id));
	}

	@Override
	public PageUsers findAll(Integer page, Integer size, String sort) {
		int totalNoUsers = userStubs.size();
		List<List<UserDTO>> listUsers = new ArrayList<>();
		for(int i = 0; i < totalNoUsers; i = i + size) {
			int limit = i + size;
			listUsers.add(userStubs.subList(i, limit > totalNoUsers ?
					totalNoUsers : limit));
		}

		List<UserDTO> finalList = listUsers.size() > page ? listUsers.get(page)
				: Collections.emptyList();
		finalList.forEach(u -> u.setPassword(null));

		PageUsers pageUsers = new PageUsers();
		pageUsers.setCurrentPage(page);
		pageUsers.setPageSize(finalList.size());
		pageUsers.setTotalNoUsers(totalNoUsers);
		pageUsers.setTotalPages(listUsers.size());
		pageUsers.setUsers(finalList);
		return pageUsers;
	}

	@Override
	public boolean updateUser(Integer id, List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");

		UserDTO user = getUser(id);
		for(PatchDocument patch : patchDocuments)
			if(!applyPatch(patch, user))
				return false;
		return true;
	}

	private boolean applyPatch(PatchDocument patch, UserDTO user) {
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
				value = StatusEnum.fromValue((String) value);

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
}
