package com.example.dream_games_demo.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateCountryRequest {
    @JsonProperty("country_name")
    private String country_name;

    public CreateCountryRequest(String country_name) {
        this.country_name = country_name;
    }

    public String getCountryName() { return this.country_name; }
    public void setCountryName(String country_name) { this.country_name = country_name; }
}
