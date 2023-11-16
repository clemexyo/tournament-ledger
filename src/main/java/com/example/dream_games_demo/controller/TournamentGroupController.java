package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidGetLeaderBoardRequestException;
import com.example.dream_games_demo.exceptions.InvalidUpdatePlayerLevelRequestException;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.requests.GetLeaderBoardRequest;
import com.example.dream_games_demo.service.TournamentGroupService;
import com.example.dream_games_demo.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/tournament-group")
public class TournamentGroupController {
    @Autowired
    private TournamentGroupService tournamentGroupService;

    @GetMapping("/leader-board/{tournament_id}/{tournament_group_id}")
    public ResponseEntity<String> getGroupLeaderBoard(
            @PathVariable(name = "tournament_id") Long tournament_id,
            @PathVariable(name = "tournament_group_id") Long tournament_group_id){
        if(tournament_id == null || tournament_group_id == null){
            throw new InvalidGetLeaderBoardRequestException();
        }
        TournamentGroup tournamentGroup = tournamentGroupService.findById(tournament_group_id);

        String leaderBoard = tournamentGroupService.generateLeaderBoard(tournamentGroup, tournament_id);
        return new ResponseEntity<String>(leaderBoard, HttpStatus.OK);
    }
}
