package com.adascal.recipesservice.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
public class RecipeResponse {

    private String title;
    private String description;
    private List<Ingredient> ingredients;
    private boolean isVegetarian;
    private int servings;
    private Duration preparationTime;
}
