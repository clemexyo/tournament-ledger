package com.example.dream_games_demo.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetLeaderBoardRequest {
    private Long tournament_group_id;
    private Long tournament_id;
    public Long getTournament_group_id() { return tournament_group_id; }
    public void setTournament_group_id(Long id) { this.tournament_group_id = id; }
    public Long getTournament_id() { return this.tournament_id; }
    public void setTournament_id(Long id) { this.tournament_id = id; }
}
