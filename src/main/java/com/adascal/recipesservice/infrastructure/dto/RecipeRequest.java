package com.adascal.recipesservice.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RecipeRequest {

    @NotBlank
    @Size(max = 50)
    private String title;
    @NotBlank
    @Size(max = 1000)
    private String description;
    @NotEmpty
    @Valid
    private List<Ingredient> ingredients;
    @NotNull
    private Duration preparationTime;
    @NotNull
    private Boolean isVegetarian;
    @NotNull
    @Min(1)
    private Integer servings;
}