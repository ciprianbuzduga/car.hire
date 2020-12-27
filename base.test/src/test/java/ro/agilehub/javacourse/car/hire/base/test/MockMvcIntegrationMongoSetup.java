package ro.agilehub.javacourse.car.hire.base.test;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MockMvcIntegrationMongoSetup {

    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
	private MongoTemplate mongoTemplate;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @After
	public void cleanCollections() {
    	for(String collection : getDroppedCollections())
    		mongoTemplate.dropCollection(collection);
	}

    protected abstract String[] getDroppedCollections();

    //TODO move into utility class
    public static String getPath(String spec)
			throws MalformedURLException {
		URL url = new URL(spec);
		String path = url.getPath();
		return path;
	}
}
