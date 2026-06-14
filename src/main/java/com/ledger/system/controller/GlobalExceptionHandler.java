package com.ledger.system.controller;

import com.ledger.system.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("message", ex.getMessage());
        response.put("code", ex.getErrorCode());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Internal server error");

        return new ResponseEntity<>(response,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
