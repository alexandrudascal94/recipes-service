package com.adascal.recipesservice.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorRule {
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    TECHNICAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    RECIPE_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_INPUT(HttpStatus.BAD_REQUEST);

    private final HttpStatus httpStatus;
}
