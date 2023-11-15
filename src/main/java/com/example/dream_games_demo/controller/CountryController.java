package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidCreateCountryRequestException;
import com.example.dream_games_demo.requests.CreateCountryRequest;
import com.example.dream_games_demo.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
