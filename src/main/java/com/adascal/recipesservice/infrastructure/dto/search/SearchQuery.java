package com.adascal.recipesservice.infrastructure.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class SearchQuery {
    @NotBlank
    private String key;
    @NotNull
    private SearchPredicate predicate;
    @NotNull
    private String value;
}
