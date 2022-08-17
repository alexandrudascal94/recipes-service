package com.adascal.recipesservice.infrastructure.mapper;

import com.adascal.recipesservice.domain.model.Recipe;
import com.adascal.recipesservice.infrastructure.dto.RecipeRequest;
import com.adascal.recipesservice.infrastructure.dto.RecipeResponse;
import com.adascal.recipesservice.infrastructure.dto.search.SearchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface RecipeMapper {
    RecipeResponse recipeToRecipeResponse(Recipe recipe);

    Recipe recipeToRecipeResponse(RecipeRequest recipe);

    default Criteria searchRequestToCriteria(String userId, SearchRequest searchRequest) {

        List<Criteria> criteriaList = searchRequest.getCriteria()
                .stream()
                .map(search -> search.getPredicate()
                        .mapCriteria(Criteria.where(search.getKey()), search.getValue()))
                .collect(Collectors.toList());

        return Criteria.where("userId").is(userId).andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }
}

