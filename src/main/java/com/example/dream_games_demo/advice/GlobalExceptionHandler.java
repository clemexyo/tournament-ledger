package com.example.dream_games_demo.advice;

import com.example.dream_games_demo.exceptions.InvalidCreateCountryRequestException;
import com.example.dream_games_demo.exceptions.InvalidCreatePlayerRequestException;
import com.example.dream_games_demo.exceptions.InvalidUpdatePlayerLevelRequestException;
import com.example.dream_games_demo.exceptions.NoPlayerFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${error.messages.invalidCreateCountryRequestMessage}")
    private String invalidCreateCountryRequestMessage;
    @Value("${error.messages.invalidCreatePlayerRequestMessage}")
    private String invalidCreatePlayerRequestMessage;
    @Value("${error.messages.invalidUpdatePlayerLevelRequestMessage}")
    private String invalidUpdatePlayerLevelRequestMessage;
    @Value("${error.messages.noPlayerFoundMessage}")
    private String noPlayerFoundMessage;

    @ExceptionHandler(NoPlayerFoundException.class)
    public ResponseEntity<String> handleNoPlayerFoundException(NoPlayerFoundException e){
        return new ResponseEntity<>(noPlayerFoundMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidCreateCountryRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidCreateCountryRequestException e) {
        return new ResponseEntity<>(invalidCreateCountryRequestMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidCreatePlayerRequestException.class)
    public ResponseEntity<String> handleInvalidCreatePlayerRequest(InvalidCreatePlayerRequestException e) {
        return new ResponseEntity<>(invalidCreatePlayerRequestMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidUpdatePlayerLevelRequestException.class)
    public ResponseEntity<String> handleInvalidUpdatePlayerLevelRequestException(InvalidUpdatePlayerLevelRequestException e) {
        return new ResponseEntity<>(invalidUpdatePlayerLevelRequestMessage, HttpStatus.BAD_REQUEST);
    }
}

