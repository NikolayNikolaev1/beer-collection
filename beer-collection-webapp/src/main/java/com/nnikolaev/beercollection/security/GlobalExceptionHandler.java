package com.nnikolaev.beercollection.security;

import com.nnikolaev.beercollection.dto.response.error.ValidationErrorResponse;

import com.nnikolaev.beercollection.dto.response.error.*;
import com.nnikolaev.beercollection.exception.EntityInUseException;
import com.nnikolaev.beercollection.exception.OperationConflictException;
import com.nnikolaev.beercollection.exception.company.*;
import com.nnikolaev.beercollection.exception.user.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                LocalDateTime.now(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    @ExceptionHandler({
            CompanyExistsException.class,
            DataIntegrityViolationException.class,
            EntityInUseException.class,
            OperationConflictException.class,
            UserExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)

        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    @ExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex, WebRequest req) {
        ErrorResponse err = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Resource not found: " + req.getDescription(false),
                LocalDateTime.now(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid path variable format: " + ex.getValue(),
                LocalDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Required request body is missing or malformed",
                LocalDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Set<Violation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new Violation(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toSet());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                violations,
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }
}