package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidEnterTournamentRequestException;
import com.example.dream_games_demo.exceptions.InvalidUpdatePlayerLevelRequestException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.requests.EnterTournamentRequest;
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
    public ResponseEntity<String> enterTournament(@RequestBody EnterTournamentRequest request){
        if(request == null || request.getPlayerId() == null || !(request.getPlayerId() instanceof Long)){
            throw new InvalidEnterTournamentRequestException();
        }
        Long player_id = request.getPlayerId();

        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;

        Map<String, Object> playerStatus = playerService.playerStatus(player_id);

        if(playerStatus.get("message") == "Can enter"){
            Map<String, Object> tournamentStatus = tournamentService.tournamentStatus();
            if(tournamentStatus.get("message") == "Valid Tournament"){
                Map<String, Object> result = tournamentService.enterTournament(player_id, (Tournament) tournamentStatus.get("latest_tournament"));
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
