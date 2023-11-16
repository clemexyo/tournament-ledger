package com.example.dream_games_demo.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClaimRewardRequest {
    @JsonProperty("player_id")
    private Long player_id;
    public Long getPlayerId() { return this.player_id; }
    public void setPlayerId(Long id) { this.player_id = id; }
}
