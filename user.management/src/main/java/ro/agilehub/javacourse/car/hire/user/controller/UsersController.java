package ro.agilehub.javacourse.car.hire.user.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.api.model.PageUsers;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.api.specification.UsersApi;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGER')")
public class UsersController implements UsersApi {

	private final UsersService usersService;

	@Override
	public ResponseEntity<Void> createUser(@Valid UserRequestDTO userDTO) {
		String newId = usersService.addUser(userDTO);
		if(newId != null) {
			UriComponents uriComponents = UriComponentsBuilder.newInstance()
					.scheme("http").host("localhost").port(8080)
					.path("/users/{id}").buildAndExpand(newId);
			return ResponseEntity.created(uriComponents.toUri()).build();
		} else
			throw new ServerErrorException("Cannot add the user because of "
					+ "unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<Void> deleteUser(String id) {
		boolean removed = usersService.removeUserById(id);
		if(removed) {
			return ResponseEntity.noContent().build();
		} else 
			throw new ServerErrorException("Cannot remove the user " + id
					+ " because of unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<UserResponseDTO> getUser(String id) {
		UserResponseDTO user = usersService.getUser(id);
		return ResponseEntity.ok(user);
	}

	@Override
	public ResponseEntity<PageUsers> getUsers(@Min(0) @Valid Integer page,
			@Min(1) @Valid Integer size,
			@Valid String sort) {
		PageUsers pageUsers = usersService.findAll(page, size, sort);
		return ResponseEntity.ok(pageUsers);
	}

	@Override
	public ResponseEntity<Void> updateUser(String id,
			@Valid List<PatchDocument> patchDocument) {
		boolean update = usersService.updateUser(id, patchDocument);
		if(update)
			return ResponseEntity.noContent().build();
		else
			throw new ServerErrorException("Cannot update the user " + id
					+ " because of unknown reasone", (Throwable)null);
	}

}
