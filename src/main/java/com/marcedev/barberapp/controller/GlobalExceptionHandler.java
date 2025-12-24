package com.marcedev.barberapp.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity<Map<String, Object>> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Bad Request",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<Map<String, Object>> notFound(Exception ex) {
        return ResponseEntity.status(404).body(Map.of(
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }
}
