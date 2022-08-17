package com.adascal.recipesservice.infrastructure.controller;

import com.adascal.recipesservice.domain.exception.ErrorRule;
import com.adascal.recipesservice.domain.model.Recipe;
import com.adascal.recipesservice.infrastructure.dto.Ingredient;
import com.adascal.recipesservice.infrastructure.dto.RecipeRequest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static com.adascal.recipesservice.factory.RecipeMother.INVALID_USER_ID;
import static com.adascal.recipesservice.factory.RecipeMother.USER_ID;
import static com.adascal.recipesservice.factory.RecipeMother.USER_ID_2;
import static com.adascal.recipesservice.factory.RecipeMother.createRecipe;
import static com.adascal.recipesservice.factory.TestRequestFactory.createDeleteRecipeRequest;
import static com.adascal.recipesservice.factory.TestRequestFactory.createGetRecipeRequest;
import static com.adascal.recipesservice.factory.TestRequestFactory.createPostRecipeRequest;
import static com.adascal.recipesservice.factory.TestRequestFactory.createPutRecipeRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class RecipeControllerIT extends AbstractBaseIT {

    @Test
    void get_when_validRecipeAndUserId_should_return200() throws Exception {
        // given
        var recipe = createAndSaveTestRecipe(USER_ID);
        //when
        mvc.perform(createGetRecipeRequest(recipe.getId(), USER_ID))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(recipe.getTitle()));
    }

    @Test
    void get_when_invalidRecipeId_should_return404() throws Exception {
        //given
        var randomRecipeId = "2sfes3d";
        //when
        mvc.perform(createGetRecipeRequest(randomRecipeId, USER_ID))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorRule.RECIPE_NOT_FOUND.name()));
    }

    @Test
    void get_when_recipeHasDifferentUserId_should_return403() throws Exception {
        //given
        var recipe = createAndSaveTestRecipe(USER_ID);
        //when
        mvc.perform(createGetRecipeRequest(recipe.getId(), USER_ID_2))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorRule.ACCESS_DENIED.name()));
    }

    @Test
    void get_when_InvalidUserId_should_return401() throws Exception {
        //given
        var recipe = createAndSaveTestRecipe(USER_ID);
        var tooLongUserId = "sdde22222222222222222";
        //when
        mvc.perform(createGetRecipeRequest(recipe.getId(), tooLongUserId))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorRule.UNAUTHORIZED.name()));
    }

    @Test
    void delete_when_validRecipeAndUserId_should_return204() throws Exception {
        //given
        var recipe = createAndSaveTestRecipe(USER_ID);
        //when
        mvc.perform(createDeleteRecipeRequest(recipe.getId(), USER_ID))
                //then
                .andExpect(status().isNoContent());
        assertTrue(recipeRepository.findById(recipe.getId()).isEmpty());
    }

    @Test
    void delete_when_invalidUserId_should_return401() throws Exception {
        //when
        mvc.perform(createDeleteRecipeRequest("1", INVALID_USER_ID))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorRule.UNAUTHORIZED.name()));
    }

    @Test
    void post_when_validBody_should_return201() throws Exception {
        //when
        mvc.perform(createPostRecipeRequest(USER_ID, objectMapper.writeValueAsString(createRecipeRequest())))
                //then
                .andExpect(status().isCreated());

        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(1);
    }

    @Test
    void post_when_invalidBody_should_return400() throws Exception {
        //given
        RecipeRequest recipeRequest = createRecipeRequest();
        String invalidTitle = "";
        recipeRequest.setTitle(invalidTitle);

        //when
        mvc.perform(createPostRecipeRequest(USER_ID, objectMapper.writeValueAsString(recipeRequest)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorRule.INVALID_INPUT.name()));
        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(0);
    }

    @Test
    void post_when_invalidUserId_should_return401() throws Exception {
        //given
        //when
        mvc.perform(createPostRecipeRequest(INVALID_USER_ID, objectMapper.writeValueAsString(createRecipeRequest())))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorRule.UNAUTHORIZED.name()));
        assertThat(recipeRepository.findAllByUserId(INVALID_USER_ID).size()).isEqualTo(0);
    }

    @Test
    void post_when_recipeExists_should_return422() throws Exception {
        //given
        createAndSaveTestRecipe(USER_ID);
        //when
        mvc.perform(createPostRecipeRequest(USER_ID, objectMapper.writeValueAsString(createRecipeRequest())))
                //then
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(ErrorRule.RECIPE_ALREADY_EXISTS.name()));
        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(1);
    }

    @Test
    void put_when_validRequest_should_return201() throws Exception {
        //given
        var recipe = createAndSaveTestRecipe(USER_ID);

        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(1);
        //when
        mvc.perform(createPutRecipeRequest(USER_ID, recipe.getId(), objectMapper.writeValueAsString(createRecipeRequest())))
                //then
                .andExpect(status().isNoContent());
        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(1);
    }

    @Test
    void put_when_invalidRecipeId_should_return404() throws Exception {
        //given
        createAndSaveTestRecipe(USER_ID);
        String invalidRecipeId = "32";
        //when
        mvc.perform(createPutRecipeRequest(USER_ID, invalidRecipeId, objectMapper.writeValueAsString(createRecipeRequest())))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorRule.RECIPE_NOT_FOUND.name()));
        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(1);
    }

    @Test
    void put_when_invalidUserId_should_return401() throws Exception {
        //given
        var recipe = createAndSaveTestRecipe(USER_ID);
        //when
        mvc.perform(createPutRecipeRequest(INVALID_USER_ID, recipe.getId(), objectMapper.writeValueAsString(createRecipeRequest())))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorRule.UNAUTHORIZED.name()));
        assertThat(recipeRepository.findAllByUserId(USER_ID).size()).isEqualTo(1);
    }


    private Recipe createAndSaveTestRecipe(String userId) {
        return recipeRepository.save(createRecipe(userId));
    }

    private RecipeRequest createRecipeRequest() {
        return RecipeRequest.builder()
                .servings(3)
                .title("carbonara")
                .ingredients(List.of(new Ingredient("Pasta", "kg", 0.3),
                        new Ingredient("eggs", "pieces", 2),
                        new Ingredient("pepper", "g", 5),
                        new Ingredient("bacon", "g", 500)))
                .description("Boil the pasta, fry the bacon, mix teh egs with parmesan and all together")
                .isVegetarian(false)
                .preparationTime(Duration.ofMinutes(25))
                .build();
    }
}
