package com.example.dream_games_demo.advice;

import com.example.dream_games_demo.exceptions.*;
import com.example.dream_games_demo.requests.EnterTournamentRequest;
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
    @Value("${error.messages.createPlayerExceptionMessage}")
    private String createPlayerExceptionMessage;
    @Value("${error.messages.createCountryExceptionMessage}")
    private String createCountryExceptionMessage;
    @Value("${error.messages.invalidEnterTournamentRequestMessage}")
    private String invalidEnterTournamentRequestMessage;
    @Value("${error.messages.playerNotFoundMessage}")
    private String playerNotFoundMessage;

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
    @ExceptionHandler(CreatePlayerException.class)
    public ResponseEntity<String> handleCreatePlayerException(CreatePlayerException e) {
        return new ResponseEntity<>(createPlayerExceptionMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CreateCountryException.class)
    public ResponseEntity<String> handleCreateCountryException(CreateCountryException e){
        return new ResponseEntity<>(createCountryExceptionMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidEnterTournamentRequestException.class)
    public ResponseEntity<String> handleInvalidEnterTournamentRequest(InvalidEnterTournamentRequestException e){
        return new ResponseEntity<>(invalidEnterTournamentRequestMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<String> handlePlayerNotFoundException(PlayerNotFoundException e){
        return new ResponseEntity<>(playerNotFoundMessage, HttpStatus.NOT_FOUND);
    }
}

