package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.requests.CreateCountryRequest;
import com.example.dream_games_demo.service.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryService countryService;
    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper for converting objects to JSON

    @Test
    void createCountry() throws Exception {
        //given
        CreateCountryRequest request = new CreateCountryRequest("testCountry");

        
    }

    @Test
    void getCountriesLeaderBoard() {
    }

    @Test
    void getCountryLeaderBoard() {
    }
}