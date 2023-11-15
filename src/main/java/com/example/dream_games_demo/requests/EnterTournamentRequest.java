package com.example.dream_games_demo.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnterTournamentRequest {
    @JsonProperty("player_id")
    private Long player_id;
    public Long getPlayerId() { return this.player_id; }
    public void setPlayerId(Long player_id) { this.player_id = player_id; }
}
