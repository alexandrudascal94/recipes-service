package com.adascal.recipesservice.factory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TestRecipeRequestFactory {

    private static final String GET_RECIPE_ENDPOINT = "/v1/recipe/";
    private static final String DELETE_RECIPE_ENDPOINT = "/v1/recipe/";
    private static final String POST_RECIPE_ENDPOINT = "/v1/recipe";
    private static final String PUT_RECIPE_ENDPOINT = "/v1/recipe/";
    private static final String SEARCH_RECIPE_ENDPOINT = "/v1/recipe/search";

    public static  MockHttpServletRequestBuilder createGetRecipeRequest(String recipeId, String userId) {
        String path = GET_RECIPE_ENDPOINT + recipeId;
        return MockMvcRequestBuilders.get(path)
                .servletPath(path)
                .header(HttpHeaders.AUTHORIZATION, userId)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder createPostRecipeRequest(String userId, String body) {
        String path = POST_RECIPE_ENDPOINT;
        return MockMvcRequestBuilders.post(path)
                .content(body)
                .servletPath(path)
                .header(HttpHeaders.AUTHORIZATION, userId)
                .contentType(MediaType.APPLICATION_JSON);
    }
    public static MockHttpServletRequestBuilder createPutRecipeRequest(String userId, String recipeId, String body) {
        String path = PUT_RECIPE_ENDPOINT + recipeId;
        return MockMvcRequestBuilders.put(path)
                .content(body)
                .servletPath(path)
                .header(HttpHeaders.AUTHORIZATION, userId)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder createDeleteRecipeRequest(String recipeId, String userId) {
        String path = DELETE_RECIPE_ENDPOINT + recipeId;
        return MockMvcRequestBuilders.delete(path)
                .servletPath(path)
                .header(HttpHeaders.AUTHORIZATION, userId)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder createSearchRecipeRequest(String userId, String body) {
        String path = SEARCH_RECIPE_ENDPOINT;
        return MockMvcRequestBuilders.post(path)
                .content(body)
                .servletPath(path)
                .header(HttpHeaders.AUTHORIZATION, userId)
                .contentType(MediaType.APPLICATION_JSON);
    }
}
