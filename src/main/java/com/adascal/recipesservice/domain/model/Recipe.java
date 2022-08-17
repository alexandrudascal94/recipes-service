package com.adascal.recipesservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "recipes")
public class Recipe {

    @NotNull
    @Id
    private String id;
    @NotBlank
    private String userId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotEmpty
    @Valid
    private List<Ingredient> ingredients;
    private boolean isVegetarian;
    @Min(1)
    private int servings;
    @NotNull
    private Duration preparationTime;
}
