package com.adascal.recipesservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Ingredient {
    @NotBlank
    private String name;
    @NotBlank
    private String unit;
    @Min(0)
    private double count;
}
