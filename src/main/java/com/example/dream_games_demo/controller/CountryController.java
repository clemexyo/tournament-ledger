package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidCreateCountryRequestException;
import com.example.dream_games_demo.requests.CreateCountryRequest;
import com.example.dream_games_demo.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/country")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @PostMapping
    public ResponseEntity<String> createCountry(@RequestBody CreateCountryRequest request){
        if(request == null || request.getCountryName() == null){
            throw new InvalidCreateCountryRequestException();
        }
        String country_name = request.getCountryName();
        String createdCountry = countryService.createCountry(country_name);
        return new ResponseEntity<String>(createdCountry, HttpStatus.CREATED);
    }
    @GetMapping("/leaderboard/{tournament_id}")
    public ResponseEntity<String> getCountriesLeaderBoard(@PathVariable(name = "tournament_id") Long tournament_id){
        String countriesLeaderBoard = countryService.generateCountriesLeaderBoard(tournament_id);
        return new ResponseEntity<>(countriesLeaderBoard, HttpStatus.OK);
    }
}
