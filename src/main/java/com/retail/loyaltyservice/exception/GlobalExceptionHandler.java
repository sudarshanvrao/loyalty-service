package com.retail.loyaltyservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * Global exception handler to handle all exception scenarios.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND, "Data does not exist", ex);
        log.error("Exception occurred as requested data was not found : ", ex);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST, "Malformed JSON request", ex);
        log.error("Exception occurred while reading JSON : ", ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST, "Type Mismatch", ex);
        log.error("Exception occurred due args type mismatch : ", ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST, "Invalid/Inappropriate request", ex);
        log.error("Exception occurred due to invalid/inappropriate request : ", ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<Object> handleAll(
            Exception ex) {
        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR, ex);
        log.error("Exception occurred : ", ex);
        return buildResponseEntity(apiError);
    }
}
