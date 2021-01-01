package ro.agilehub.javacourse.car.hire.base.test;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class MockMvcIntegrationMongoSetup extends MockMvcSetup {

    @Autowired
	protected MongoTemplate mongoTemplate;

    @After
	public void cleanCollections() {
    	for(String collection : getDroppedCollections())
    		mongoTemplate.dropCollection(collection);
	}

    protected abstract String[] getDroppedCollections();

}
