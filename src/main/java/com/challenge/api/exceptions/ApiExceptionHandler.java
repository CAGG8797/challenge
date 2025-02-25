package com.challenge.api.exceptions;

import com.challenge.api.model.dto.APIErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new APIErrorResponse(List.of(ex.getMessage())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIErrorResponse> handleConstraintDeclaration(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(new APIErrorResponse(ex.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIErrorResponse> handleIllegalArgumentExceptions(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new APIErrorResponse(List.of(ex.getMessage())));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<APIErrorResponse> handleOutOfStockException(OutOfStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new APIErrorResponse(List.of(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleGenericException(Exception ex) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            return handleConstraintDeclaration((ConstraintViolationException) ex.getCause());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new APIErrorResponse(List.of(ex.getMessage())));
    }
}
