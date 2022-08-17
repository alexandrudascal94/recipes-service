package com.adascal.recipesservice.domain.exception;

import lombok.Getter;

@Getter
public class RecipeServiceException extends RuntimeException{
    private final ErrorRule rule;
    public RecipeServiceException(ErrorRule errorRule, String message) {
        super(message);
        rule = errorRule;
    }
}
