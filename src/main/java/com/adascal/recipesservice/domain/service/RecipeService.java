package com.adascal.recipesservice.domain.service;

import com.adascal.recipesservice.domain.exception.ErrorRule;
import com.adascal.recipesservice.domain.exception.RecipeServiceException;
import com.adascal.recipesservice.domain.model.Recipe;
import com.adascal.recipesservice.domain.repository.RecipeRepository;
import com.adascal.recipesservice.domain.repository.RecipeSearchRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipesRepository;
    private final RecipeSearchRepository recipeSearchRepository;

    public Recipe getRecipe(String userId, String recipeId) {
        var recipe = recipesRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeServiceException(ErrorRule.RECIPE_NOT_FOUND, "Recipe was not found"));

        if (!userId.equals(recipe.getUserId())) {
            throw new RecipeServiceException(ErrorRule.ACCESS_DENIED, "Access denied for current user");
        }
        return recipe;
    }

    public Recipe createRecipe(Recipe recipe, String userId) {
        if(recipesRepository.existsByTitleAndUserId(recipe.getTitle(), userId)){
            throw new RecipeServiceException(ErrorRule.RECIPE_ALREADY_EXISTS, "Recipe with the same name already exists");
        }
        recipe.setUserId(userId);
        return recipesRepository.save(recipe);
    }

    public Recipe updateRecipe(String userId, String recipeId, Recipe updatedRecipe) {
        var existingRecipe = getRecipe(userId, recipeId);
        updatedRecipe.setId(existingRecipe.getId());
        updatedRecipe.setUserId(userId);
        return recipesRepository.save(updatedRecipe);
    }

    public List<Recipe> searchRecipes(Criteria searchCriteria) {
        return recipeSearchRepository.findAll(searchCriteria);
    }

    public void deleteRecipe(String recipeId, String userId) {
        recipesRepository.deleteByRecipeIdAndUserId(recipeId, userId);
    }

    public List<Recipe> findAll(String userId) {
        return recipesRepository.findAll(userId);
    }
}
