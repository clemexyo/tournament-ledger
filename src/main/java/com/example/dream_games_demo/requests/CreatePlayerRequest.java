package com.example.dream_games_demo.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePlayerRequest {
    @JsonProperty("user_name")
    private String user_name;
    public void setUserName(String user_name) { this.user_name = user_name; }
    public String getUserName() { return this.user_name; }
}
