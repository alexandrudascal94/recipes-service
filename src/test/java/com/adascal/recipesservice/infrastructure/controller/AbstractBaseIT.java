package com.adascal.recipesservice.infrastructure.controller;

import com.adascal.recipesservice.domain.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payconiq.commons.testcontainer.mongodb.annotation.MongoDBTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MongoDBTestContainer
@AutoConfigureMockMvc
public abstract class AbstractBaseIT {

    @Autowired
    protected  MockMvc mvc;

    @Autowired
    protected RecipeRepository recipeRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        recipeRepository.deleteAll();
    }
}
