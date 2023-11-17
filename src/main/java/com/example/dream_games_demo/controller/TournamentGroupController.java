package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidGetLeaderBoardRequestException;
import com.example.dream_games_demo.exceptions.InvalidGetPlayerGroupRankException;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.service.TournamentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/player-rank/{player_id}/{tournament_group_id}")
    public ResponseEntity<String> getPlayerGroupRank(
            @PathVariable(name = "player_id") Long player_id,
            @PathVariable(name = "tournament_group_id") Long tournament_group_id) {
        if(player_id == null || tournament_group_id == null){
            throw new InvalidGetPlayerGroupRankException();
        }
        String player_rank_info = tournamentGroupService.getPlayerGroupRank(player_id, tournament_group_id);
        return new ResponseEntity<>(player_rank_info, HttpStatus.OK);
    }
}
