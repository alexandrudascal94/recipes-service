package com.adascal.recipesservice.infrastructure.error;


import com.adascal.recipesservice.domain.exception.ErrorRule;
import com.adascal.recipesservice.domain.exception.RecipeServiceException;
import com.adascal.recipesservice.infrastructure.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RecipeServiceExceptionHandler {

    @ExceptionHandler(RecipeServiceException.class)
    public ResponseEntity<ErrorResponse> handleProfileRegistryServiceException(RecipeServiceException exception) {
        log.error("recipe service exception", exception);

        return ResponseEntity.status(exception.getRule().getHttpStatus())
                .body(new ErrorResponse(exception.getRule().name(), exception.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable exception) {
        log.error("Internal server error", exception);

        var rule = ErrorRule.TECHNICAL_ERROR;
        return ResponseEntity.status(rule.getHttpStatus())
                .body(new ErrorResponse(rule.name(), "Internal technical Error"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        log.error("Validation error", exception);

        var rule = ErrorRule.INVALID_INPUT;
        return ResponseEntity.status(rule.getHttpStatus())
                .body(new ErrorResponse(rule.name(), "Invalid input"));
    }
}