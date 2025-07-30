package com.itg.accountservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itg.accountservice.common.ApiBaseResponse;
import com.itg.accountservice.common.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AppExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiBaseResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        ApiBaseResponse<Map<String, String>> response = new ApiBaseResponse<>(
                null,
                false,
                MessageConstants.VALIDATION_FAILED,
                HttpStatus.BAD_REQUEST,
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiBaseResponse<Void>> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(
                ApiBaseResponse.badRequest(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiBaseResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiBaseResponse<>(null, false, ex.getMessage(), HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiBaseResponse<Void>> handleDeserializationError(HttpMessageNotReadableException ex) {
        String message = MessageConstants.INVALID_PAYLOAD;

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException formatException = (InvalidFormatException) cause;

            if (!formatException.getPath().isEmpty()) {
                String fieldName = formatException.getPath().get(0).getFieldName();
                message = String.format(MessageConstants.INVALID_FIELD_PAYLOAD, fieldName);
            }
        }
        return new ResponseEntity<>(ApiBaseResponse.badRequest(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiBaseResponse<Void>> handleGeneralException(Exception ex) {
        logger.error(MessageConstants.UNEXPECTED_ERROR, ex.getMessage());
        return new ResponseEntity<>(
                ApiBaseResponse.serverError("Internal Server Error: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
