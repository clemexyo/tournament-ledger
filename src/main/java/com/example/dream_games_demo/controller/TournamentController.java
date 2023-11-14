package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.service.PlayerService;
import com.example.dream_games_demo.service.TournamentService;
import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ResponseEntity<String> enterTournament(@RequestBody Map<String, Object> requestBody){
        Long playerId = ((Number) requestBody.get("playerId")).longValue();

        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;

        Map<String, Object> playerStatus = playerService.playerStatus(playerId);

        if(playerStatus.get("message") == "Can enter"){
            Map<String, Object> tournamentStatus = tournamentService.tournamentStatus();
            if(tournamentStatus.get("message") == "Valid Tournament"){
                Map<String, Object> result = tournamentService.enterTournament(playerId);
                message = (String) result.get("message");
                httpStatus = (HttpStatus) result.get("httpStatus");
            }
            else{
                message = (String) tournamentStatus.get("message");
                httpStatus = (HttpStatus) tournamentStatus.get("httpStatus");
            }
        }
        else{
            message = (String) playerStatus.get("message");
            httpStatus = (HttpStatus) playerStatus.get("httpStatus");
        }

        return new ResponseEntity<String>(message, httpStatus);

    }
}
