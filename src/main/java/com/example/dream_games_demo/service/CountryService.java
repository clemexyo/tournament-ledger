package com.example.dream_games_demo.service;

import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public String createCountry(String country_name){
        Country newCountry = new Country(country_name);
        countryRepository.save(newCountry);
        return newCountry.toString();
    }
    public List<Country> allCountries(){
        return countryRepository.findAll();
    }
}
