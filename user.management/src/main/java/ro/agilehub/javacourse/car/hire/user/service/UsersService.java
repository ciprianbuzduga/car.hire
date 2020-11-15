package ro.agilehub.javacourse.car.hire.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.UserDTO;

@Service
public interface UsersService {

	boolean addUser(UserDTO userDTO);

	boolean removeUserById(Integer id);

	UserDTO getUser(Integer id);

	PageUsers findAll(Integer page, Integer size, String sort);

	boolean updateUser(Integer id, List<PatchDocument> patchDocuments);

}
