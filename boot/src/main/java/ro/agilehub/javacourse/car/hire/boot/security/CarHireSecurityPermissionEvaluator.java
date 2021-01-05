package ro.agilehub.javacourse.car.hire.boot.security;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;
import ro.agilehub.javacourse.car.hire.rental.repository.RentalRepository;

@Component
@RequiredArgsConstructor
public class CarHireSecurityPermissionEvaluator implements PermissionEvaluator {

	private static final List<String> allowedAuthorities = List.of("ADMIN",
			"MANAGER", "CUSTOMER");

	private final RentalRepository repository;

	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		Jwt jwt = (Jwt) authentication.getPrincipal();
		String userName = jwt.getClaim("user_name");
		Optional<RentalDoc> optRental = repository.findById((String) targetId);
		//delegate to service to handle
		if(optRental.isEmpty())
			return true;

		RentalDoc rentalDoc = optRental.get();
		String createdBy = rentalDoc.getCreatedBy();
		if(createdBy != null && !createdBy.equals(userName)) {
			System.out.println(">>>>>>>>> CreatedBy doesn't match with " + userName);
			return false;
		}
		String scope = (String) permission;
		List<String> scopes = jwt.getClaim("scope");
		if((scopes == null || scopes.isEmpty()) 
				|| !scopes.contains(scope.toLowerCase())) {
			System.out.println(">>>>>>>>> User " + userName + " doesn't haved scope "
					+ scope);
			return false;
		}
		List<String> authorities = jwt.getClaim("authorities");
		if(authorities == null || authorities.isEmpty()) {
			System.out.println("No authorities found for user " + userName);
			return false;
		}
		for(String auth: authorities) {
			boolean match = allowedAuthorities.stream().anyMatch(
					allowedAuth -> allowedAuth.equals(auth));
			if(match) {
				System.out.println("Found at least Auth: " + auth);
				return true;
			}
		}
		return false;
	}

}
