package ro.agilehub.javacourse.car.hire.user.service;

import java.util.List;

import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

public interface UsersService {

	String addUser(UserRequestDTO userDTO);

	boolean removeUserById(String id);

	UserResponseDTO getUser(String id);

	PageUsers findAll(Integer page, Integer size, String sort);

	boolean updateUser(String id, List<PatchDocument> patchDocuments);

	UserDoc getUserDoc(String userId);

}
