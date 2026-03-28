package com.example.resinoracle.web;

import com.example.resinoracle.dto.ErrorResponse;
import com.example.resinoracle.service.InvalidStateAbbreviationException;
import com.example.resinoracle.service.StateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StateExceptionHandler {

    @ExceptionHandler(InvalidStateAbbreviationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStateAbbreviation(InvalidStateAbbreviationException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_STATE_ABBREVIATION", ex.getMessage()));
    }

    @ExceptionHandler(StateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStateNotFound(StateNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("STATE_NOT_FOUND", ex.getMessage()));
    }
}

