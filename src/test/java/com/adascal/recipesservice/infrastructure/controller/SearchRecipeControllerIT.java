package com.adascal.recipesservice.infrastructure.controller;

import com.adascal.recipesservice.domain.exception.ErrorRule;
import com.adascal.recipesservice.domain.model.Recipe;
import com.adascal.recipesservice.infrastructure.dto.Ingredient;
import com.adascal.recipesservice.infrastructure.dto.RecipeResponse;
import com.adascal.recipesservice.infrastructure.dto.search.SearchPredicate;
import com.adascal.recipesservice.infrastructure.dto.search.SearchQuery;
import com.adascal.recipesservice.infrastructure.dto.search.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static com.adascal.recipesservice.factory.RecipeObjectMother.INVALID_USER_ID;
import static com.adascal.recipesservice.factory.RecipeObjectMother.USER_ID;
import static com.adascal.recipesservice.factory.RecipeObjectMother.createRecipe;
import static com.adascal.recipesservice.factory.RecipeObjectMother.createRecipes;
import static com.adascal.recipesservice.factory.TestRecipeRequestFactory.createSearchRecipeRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchRecipeControllerIT extends AbstractBaseIT {

    @Test
    void search_with_noCriteria_should_ReturnAllRecipes() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var searchRequest = new SearchRequest(List.of());
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.size()).isEqualTo(response.length);
    }

    @Test
    void search_when_invalidUserId_should_return401() throws Exception {
        //given
        var recipe = createAndSaveTestRecipe(USER_ID);
        var search = SearchQuery.builder()
                .key("title")
                .predicate(SearchPredicate.EQUAL)
                .value(recipe.getTitle())
                .build();
        var searchRequest = new SearchRequest(List.of(search));
        //when
        mvc.perform(createSearchRecipeRequest(INVALID_USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorRule.UNAUTHORIZED.name()));
    }

    @Test
    void search_allVegetarian_should_return_vegetarianRecipes() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var search = SearchQuery.builder()
                .key("isVegetarian")
                .predicate(SearchPredicate.EQUAL)
                .value(String.valueOf(true))
                .build();
        var searchRequest = new SearchRequest(List.of(search));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());

        assertThat(recipes.stream().filter(Recipe::isVegetarian).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).allMatch(RecipeResponse::isVegetarian));
    }

    @Test
    void search_allNonVegetarian_should_return_nonVegetarianRecipes() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var search = SearchQuery.builder()
                .key("isVegetarian")
                .predicate(SearchPredicate.EQUAL)
                .value(String.valueOf(false))
                .build();
        var searchRequest = new SearchRequest(List.of(search));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());

        assertThat(recipes.stream().filter(r -> !r.isVegetarian()).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).noneMatch(RecipeResponse::isVegetarian));
    }

    @Test
    void search_with_ingredient_shouldReturn_recipesWithIngredient() throws Exception {
        //given
        recipeRepository.saveAll(createRecipes(USER_ID));
        String ingredient = "eggs";
        var search = SearchQuery.builder()
                .key("ingredients")
                .predicate(SearchPredicate.INCLUDE)
                .value(ingredient)
                .build();
        var searchRequest = new SearchRequest(List.of(search));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertTrue(Arrays.stream(response)
                .allMatch(r -> containsIngredient(r.getIngredients(), ingredient)));
    }

    @Test
    void search_without_ingredient_shouldReturn_recipesWithoutIngredient() throws Exception {
        //given
        recipeRepository.saveAll(createRecipes(USER_ID));
        var ingredient = "eggs";
        var search = SearchQuery.builder()
                .key("ingredients")
                .predicate(SearchPredicate.EXCLUDE)
                .value(ingredient)
                .build();
        var searchRequest = new SearchRequest(List.of(search));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());

        assertTrue(Arrays.stream(response).noneMatch(r -> containsIngredient(r.getIngredients(), ingredient)));
    }

    @Test
    void search_when_description_containsText_shouldReturn_recipesWithThatText() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var text = "oven";

        var containsTextQuery = SearchQuery.builder()
                .key("description")
                .predicate(SearchPredicate.CONTAINS_TEXT)
                .value(text)
                .build();

        var searchRequest = new SearchRequest(List.of(containsTextQuery));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());

        assertThat(recipes.stream().filter(r -> r.getDescription().contains(text)).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getDescription().contains(text)));
    }

    @Test
    void search_when_description_doesntContainText_shouldReturn_recipesWithoutThatText() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var text = "oven";

        var containsTextQuery = SearchQuery.builder()
                .key("description")
                .predicate(SearchPredicate.NOT_CONTAINS_TEXT)
                .value(text)
                .build();

        var searchRequest = new SearchRequest(List.of(containsTextQuery));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.stream().filter(r -> !r.getDescription().contains(text)).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).noneMatch(recipe -> recipe.getDescription().contains(text)));
    }

    @Test
    void search_when_servings_greater_shouldReturn_recipesWithGreaterServings() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var servings = 2;

        var query = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.GREATER)
                .value(String.valueOf(servings))
                .build();

        var searchRequest = new SearchRequest(List.of(query));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.stream().filter(r -> r.getServings() > servings).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getServings() > servings));
    }

    @Test
    void search_when_servings_less_shouldReturn_recipesWithLessServings() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var servings = 3;

        var query = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.LESS)
                .value(String.valueOf(servings))
                .build();

        var searchRequest = new SearchRequest(List.of(query));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.stream().filter(r -> r.getServings() < servings).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getServings() < servings));
    }

    @Test
    void search_when_servings_equal_shouldReturn_recipesWithThatServings() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var servings = 3;

        var query = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.EQUAL)
                .value(String.valueOf(servings))
                .build();

        var searchRequest = new SearchRequest(List.of(query));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.stream().filter(r -> r.getServings() == servings).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getServings() == servings));
    }

    @Test
    void search_when_servings_notEqual_shouldReturn_recipesWithoutThatServings() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var servings = 3;

        var query = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.NOT_EQUAL)
                .value(String.valueOf(servings))
                .build();

        var searchRequest = new SearchRequest(List.of(query));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.stream().filter(r -> r.getServings() != servings).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getServings() != servings));
    }

    @Test
    void search_when_invalidInput_greaterPredicate_should_return400() throws Exception {
        //given
        recipeRepository.saveAll(createRecipes(USER_ID));
        var servings = "two";

        var query = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.GREATER)
                .value(servings)
                .build();

        var searchRequest = new SearchRequest(List.of(query));
        //when
        mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorRule.INVALID_INPUT.name()));
    }

    @Test
    void search_when_invalidInput_lessPredicate_should_return400() throws Exception {
        //given
        recipeRepository.saveAll(createRecipes(USER_ID));
        var servings = "two";

        var query = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.LESS)
                .value(servings)
                .build();

        var searchRequest = new SearchRequest(List.of(query));
        //when
        mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorRule.INVALID_INPUT.name()));
    }


    @Test
    void search_when_title_equal_should_returnTheRecipeWithThatTitle() throws Exception {
        //given
        var recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var title = "carbonara";

        var searchTitleQuery = SearchQuery.builder()
                .key("title")
                .predicate(SearchPredicate.EQUAL)
                .value(title)
                .build();
        var searchRequest = new SearchRequest(List.of(searchTitleQuery));
        //when
        var requestResponse = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn().getResponse();

        RecipeResponse[] response = parseSearchResponse(requestResponse);
        assertThat(response.length).isEqualTo(1);
        assertThat(response[0].getTitle()).isEqualTo(title);
    }

    @Test
    void search_when_title_notEqual_should_returnTheRecipeWithoutThatTitle() throws Exception {
        //given
        List<Recipe> recipes = createRecipes(USER_ID);
        recipeRepository.saveAll(recipes);
        var title = "carbonara";

        var notEqualQuery = SearchQuery.builder()
                .key("title")
                .predicate(SearchPredicate.NOT_EQUAL)
                .value(title)
                .build();

        var searchRequest = new SearchRequest(List.of(notEqualQuery));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());
        assertThat(recipes.stream().filter(r -> !r.getTitle().equals(title)).count()).isEqualTo(response.length);
        assertTrue(Arrays.stream(response).noneMatch(recipe -> recipe.getTitle().equals(title)));
    }

    @Test
    void search_with_MultipleCriteria_should_ReturnRecipes() throws Exception {
        //given
        recipeRepository.saveAll(createRecipes(USER_ID));
        var ingredient = "salt";
        var descriptionText = "boil";
        var servings = "2";
        var searchIngredients = SearchQuery.builder()
                .key("ingredients")
                .predicate(SearchPredicate.EXCLUDE)
                .value(ingredient)
                .build();

        var searchDescription = SearchQuery.builder()
                .key("description")
                .predicate(SearchPredicate.CONTAINS_TEXT)
                .value(descriptionText)
                .build();

        var searchServings = SearchQuery.builder()
                .key("servings")
                .predicate(SearchPredicate.GREATER)
                .value(servings)
                .build();

        var searchRequest = new SearchRequest(List.of(searchDescription, searchIngredients, searchServings));
        //when
        var result = mvc.perform(createSearchRecipeRequest(USER_ID, objectMapper.writeValueAsString(searchRequest)))
                //then
                .andExpect(status().isOk())
                .andReturn();

        RecipeResponse[] response = parseSearchResponse(result.getResponse());

        assertTrue(Arrays.stream(response).noneMatch(r -> containsIngredient(r.getIngredients(), ingredient)));
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getServings() > Integer.parseInt(servings)));
        assertTrue(Arrays.stream(response).allMatch(recipe -> recipe.getDescription().contains(descriptionText)));
    }

    private RecipeResponse[] parseSearchResponse(MockHttpServletResponse response2) throws Exception {
        return objectMapper.readValue(response2.getContentAsString(), RecipeResponse[].class);
    }

    private boolean containsIngredient(List<Ingredient> ingredients, String ingredient) {
        return ingredients.stream().anyMatch(i -> i.getName().equals(ingredient));
    }

    private Recipe createAndSaveTestRecipe(String userId) {
        return recipeRepository.save(createRecipe(userId));
    }
}
