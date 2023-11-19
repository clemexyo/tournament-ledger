package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidGetLeaderBoardRequestException;
import com.example.dream_games_demo.exceptions.InvalidGetPlayerGroupRankException;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.service.TournamentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tournament-group")
public class TournamentGroupController {
    @Autowired
    private TournamentGroupService tournamentGroupService;

    @GetMapping("/leaderboard")
    public ResponseEntity<String> getGroupLeaderBoard(
            @RequestParam(name = "tournament_id") Long tournament_id,
            @RequestParam(name = "tournament_group_id") Long tournament_group_id){
        if(tournament_id == null || tournament_group_id == null){
            throw new InvalidGetLeaderBoardRequestException();
        }
        TournamentGroup tournamentGroup = tournamentGroupService.findById(tournament_group_id);

        String leaderBoard = tournamentGroupService.generateLeaderBoard(tournamentGroup);
        return new ResponseEntity<>(leaderBoard, HttpStatus.OK);
    }
    @GetMapping("/player-rank")
    public ResponseEntity<String> getPlayerGroupRank(
            @RequestParam(name = "player_id") Long player_id,
            @RequestParam(name = "tournament_group_id") Long tournament_group_id) {
        if(player_id == null || tournament_group_id == null){
            throw new InvalidGetPlayerGroupRankException();
        }
        String playerRankInfo = tournamentGroupService.getPlayerGroupRank(player_id, tournament_group_id);
        return new ResponseEntity<>(playerRankInfo, HttpStatus.OK);
    }
}
