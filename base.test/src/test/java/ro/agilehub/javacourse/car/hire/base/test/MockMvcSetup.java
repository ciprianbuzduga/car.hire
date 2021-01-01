package ro.agilehub.javacourse.car.hire.base.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MockMvcSetup {
	protected static final String PATH_USERS = "/users";
	protected static final String PATH_CARS = "/cars";
	protected static final String PATH_RENTALS = "/rentals";

	protected static final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor ADMIN =
			user("jack").authorities(new SimpleGrantedAuthority("ADMIN"));
	protected static final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor MANAGER =
			user("john").authorities(new SimpleGrantedAuthority("MANAGER"));
	protected static final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor CUSTOMER =
			user("daniel").authorities(new SimpleGrantedAuthority("CUSTOMER"));

    protected MockMvc mvc;
    
    @Autowired
    protected WebApplicationContext context;
    
    @Autowired
    protected ObjectMapper objectMapper;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    protected SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor getRandomRole() {
		SimpleGrantedAuthority[] roles = new SimpleGrantedAuthority[] {
				new SimpleGrantedAuthority("ADMIN"),
				new SimpleGrantedAuthority("MANAGER"),
				new SimpleGrantedAuthority("CUSTOMER") };
		
		int randomIdx = new Random().ints(0, 3).findFirst().getAsInt();
		return user("jack_" + randomIdx).authorities(roles[randomIdx]);
	}

    //TODO move into utility class
    public static String getPath(String spec)
			throws MalformedURLException {
		URL url = new URL(spec);
		String path = url.getPath();
		return path;
	}
}
