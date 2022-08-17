package com.adascal.recipesservice.infrastructure.controller;


import com.adascal.recipesservice.domain.model.Recipe;
import com.adascal.recipesservice.domain.service.RecipeService;
import com.adascal.recipesservice.infrastructure.dto.RecipeRequest;
import com.adascal.recipesservice.infrastructure.dto.RecipeResponse;
import com.adascal.recipesservice.infrastructure.dto.search.SearchRequest;
import com.adascal.recipesservice.infrastructure.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.adascal.recipesservice.infrastructure.validation.UserValidation.validateUserId;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/recipe")
public class RecipeController {

    private final String RECIPE_PATH_ROOT = "/v1/recipe/";

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<RecipeResponse> get(@RequestHeader(HttpHeaders.AUTHORIZATION) String userId,
                                              @PathVariable("id") String recipeId) {
        validateUserId(userId);
        var recipe = recipeService.getRecipe(userId, recipeId);
        return ResponseEntity.ok(recipeMapper.recipeToRecipeResponse(recipe));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String userId,
                                       @PathVariable("id") String recipeId) {
        validateUserId(userId);
        recipeService.deleteRecipe(recipeId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestHeader(HttpHeaders.AUTHORIZATION) String userId,
                                    @Valid @RequestBody RecipeRequest recipeRequest) {
        validateUserId(userId);
        var recipe = recipeService.createRecipe(recipeMapper.recipeToRecipeResponse(recipeRequest), userId);
        var uri = URI.create(RECIPE_PATH_ROOT + recipe.getId());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(@RequestHeader(HttpHeaders.AUTHORIZATION) String userId,
                                       @PathVariable("id") String recipeId,
                                       @Valid @RequestBody RecipeRequest recipeRequest) {
        validateUserId(userId);
        recipeService.updateRecipe(userId, recipeId, recipeMapper.recipeToRecipeResponse(recipeRequest));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<RecipeResponse>> search(@RequestHeader(HttpHeaders.AUTHORIZATION) String userId,
                                                       @Valid @RequestBody SearchRequest searchRequest) {
        validateUserId(userId);
        List<Recipe> recipes;

        if (searchRequest.getCriteria().isEmpty()) {
            recipes = recipeService.findAll(userId);
        } else {
            var searchCriteria = recipeMapper.searchRequestToCriteria(userId, searchRequest);
            recipes = recipeService.searchRecipes(searchCriteria);

        }

        List<RecipeResponse> recipeResponses = recipes.stream()
                .map(r -> recipeMapper.recipeToRecipeResponse(r))
                .collect(Collectors.toList());

        return ResponseEntity.ok(recipeResponses);
    }
}
