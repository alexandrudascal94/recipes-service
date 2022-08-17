package com.adascal.recipesservice.infrastructure.initializers;

import com.adascal.recipesservice.domain.model.Ingredient;
import com.adascal.recipesservice.domain.model.Recipe;
import com.adascal.recipesservice.domain.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DataLoader {

    private final RecipeRepository recipeRepository;

    @PostConstruct
    void loadDataIntoDatabase() {
        recipeRepository.saveAll(createRecipes());
        log.info("Inserted new recipes in the database");
    }

    private List<Recipe> createRecipes() {
        var recipe1 = Recipe.builder()
                .id("1")
                .userId("1")
                .servings(3)
                .title("carbonara")
                .ingredients(List.of(new Ingredient("pasta", "kg", 0.3),
                        new Ingredient("eggs", "pieces", 2),
                        new Ingredient("pepper", "g", 5),
                        new Ingredient("bacon", "g", 500)))
                .description("boil the pasta, fry the bacon, mix teh egs with parmesan and all together")
                .isVegetarian(false)
                .preparationTime(Duration.ofMinutes(25))
                .build();
        var recipe2 = Recipe.builder()
                .id("2")
                .userId("1")
                .servings(2)
                .title("baked potatoes")
                .ingredients(List.of(new Ingredient("potatoes", "kg", 0.5),
                        new Ingredient("salt", "g", 5),
                        new Ingredient("pepper", "g", 5),
                        new Ingredient("oil", "ml", 20)))
                .description("clean the potatoes, season with all condiments, bake in the oven at 220 C for 45 minutes")
                .isVegetarian(true)
                .preparationTime(Duration.ofMinutes(60))
                .build();

        var recipe3 = Recipe.builder()
                .id("3")
                .userId("1")
                .servings(3)
                .title("tea")
                .ingredients(List.of(new Ingredient("water", "ml", 900),
                        new Ingredient("tea", "g", 15)))
                .description("boil the water, infuse teh tea leaves for 3 minutes")
                .isVegetarian(true)
                .preparationTime(Duration.ofMinutes(6))
                .build();

        var recipe4 = Recipe.builder()
                .id("4")
                .userId("1")
                .servings(3)
                .title("boiled eggs")
                .ingredients(List.of(new Ingredient("eggs", "pieces", 9),
                        new Ingredient("water", "liter", 2)))
                .description("Boil the water, place carefully the eggs with a spun in the water and boil for 6 minutes")
                .isVegetarian(false)
                .preparationTime(Duration.ofMinutes(10))
                .build();
        return List.of(recipe1, recipe2, recipe3, recipe4);
    }
}
