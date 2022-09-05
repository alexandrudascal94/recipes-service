package com.adascal.recipesservice.domain.repository;

import com.adascal.recipesservice.domain.model.Recipe;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class RecipeSearchRepository {

    private final MongoTemplate mongoTemplate;

    public List<Recipe> findAll(Criteria criteria) {
        return mongoTemplate.find(new Query(criteria), Recipe.class);
    }
}
