package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/country")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @PostMapping
    public ResponseEntity<String> createCountry(@RequestBody Map<String, Object> requestBody){
        String country_name = (String) requestBody.get("country_name");
        String createdCountry = countryService.createCountry(country_name);
        return new ResponseEntity<String>(createdCountry, HttpStatus.CREATED);
    }
}
