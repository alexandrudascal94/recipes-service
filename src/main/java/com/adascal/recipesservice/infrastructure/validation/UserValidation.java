package com.adascal.recipesservice.infrastructure.validation;

import com.adascal.recipesservice.domain.exception.ErrorRule;
import com.adascal.recipesservice.domain.exception.RecipeServiceException;

import java.util.regex.Pattern;

public class UserValidation {

    private static final String USER_ID_PATTERN = "[a-zA-Z0-9]{1,10}";

    public static void validateUserId(String userId) {
        if (!Pattern.matches(USER_ID_PATTERN, userId)) {
            throw new RecipeServiceException(ErrorRule.UNAUTHORIZED, "Invalid authentication credentials");
        }
    }
}
