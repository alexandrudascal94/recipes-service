package com.adascal.recipesservice;

import com.payconiq.commons.testcontainer.mongodb.annotation.MongoDBTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MongoDBTestContainer
class RecipesServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
