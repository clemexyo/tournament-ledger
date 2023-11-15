package com.example.dream_games_demo.advice;

import com.example.dream_games_demo.exceptions.InvalidCreateCountryRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${error.messages.invalidCreateCountryRequestMessage}")
    private String invalidCreateCountryRequestMessage;

    @ExceptionHandler(InvalidCreateCountryRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidCreateCountryRequestException ex) {
        return new ResponseEntity<>(invalidCreateCountryRequestMessage, HttpStatus.BAD_REQUEST);
    }
}

