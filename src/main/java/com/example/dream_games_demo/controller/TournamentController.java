package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidEnterTournamentRequestException;
import com.example.dream_games_demo.exceptions.InvalidUpdatePlayerLevelRequestException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.requests.EnterTournamentRequest;
import com.example.dream_games_demo.service.PlayerService;
import com.example.dream_games_demo.service.TournamentGroupService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TournamentGroupService tournamentGroupService;

    @PostMapping
    public ResponseEntity<String> enterTournament(@RequestBody EnterTournamentRequest request){
        if(request == null || request.getPlayerId() == null || !(request.getPlayerId() instanceof Long)){
            throw new InvalidEnterTournamentRequestException();
        }
        Long player_id = request.getPlayerId();
        TournamentGroup tournamentGroup = null;
        boolean playerStatus = playerService.playerStatus(player_id);

        if(playerStatus){
            Tournament tournamentStatus = tournamentService.tournamentStatus(); //if the latest tournament exists and valid, returns the latest tournament.
                                                                                // If not, throws the appropriate exception.
            tournamentGroup = tournamentService.enterTournament(player_id, tournamentStatus);
        }
        String leaderboard = tournamentGroupService.generateLeaderBoard(tournamentGroup);
        return new ResponseEntity<String>(leaderboard, HttpStatus.OK);
    }
}
