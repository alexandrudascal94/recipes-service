package com.adascal.recipesservice.domain.repository;

import com.adascal.recipesservice.domain.model.Recipe;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends PagingAndSortingRepository<Recipe, String>{

    @Query("{ 'id' : ?0}")
    Optional<Recipe> findById(String recipeId);

    @Query(value = "{ 'id' : ?0, 'userId' : ?1}", delete = true)
    void deleteByRecipeIdAndUserId(String recipeId, String userId);

    @ExistsQuery("{ 'title': ?0, 'userId': ?1}")
    boolean existsByTitleAndUserId(String title, String userId);

    @ExistsQuery("{ 'userId': ?0}")
    List<Recipe> findAllByUserId(String userId);

    @Query("{ 'userId' : ?0}")
    List<Recipe> findAll(String userId);

}

