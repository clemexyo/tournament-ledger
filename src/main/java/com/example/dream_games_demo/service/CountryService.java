package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.CountryNotFoundException;
import com.example.dream_games_demo.exceptions.CreateCountryException;
import com.example.dream_games_demo.exceptions.NoCountryFoundException;
import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private RewardsService rewardsService;
    public String createCountry(String country_name){
        try {
            Country newCountry = new Country(country_name);
            countryRepository.save(newCountry);
            return newCountry.toString();
        }catch (Exception e){
            throw new CreateCountryException();
        }
    }
    public List<Country> allCountries(){
        List<Country> allCountries = countryRepository.findAll();
        if(allCountries.isEmpty()){
            throw new NoCountryFoundException();
        }
        return allCountries;
    }
    public Country findCountryById(Long id) {
        Optional<Country> optionalCountry = countryRepository.findById(id);
        if(!optionalCountry.isPresent()) {
            throw new CountryNotFoundException();
        }
        return optionalCountry.get();
    }
    public String generateCountriesLeaderBoard(Long tournament_id){
        List<Rewards> allScoresOfTheTournament = rewardsService.getScoresByTournament(tournament_id);
        List<Country> allCountries = allCountries();

        Map<String, Long> CountryToScoreMap = new HashMap<>();
        for(Country currentCountry : allCountries){
            Long total_score_of_current_country = 0L;
            for(Rewards currentScore : allScoresOfTheTournament){
                if(Objects.equals(currentCountry.getName(), currentScore.getPlayer().getCountry())) {
                    total_score_of_current_country += currentScore.getScore();
                }
            }
            CountryToScoreMap.put(currentCountry.getName(), total_score_of_current_country);
        }
        Map<String, Long> sortedByScore = sortMapByValue(CountryToScoreMap);
        String countriesLeaderBoard = "Countries from highest score to lowest:\n";
        for(Map.Entry<String, Long> entry : sortedByScore.entrySet()) {
            countriesLeaderBoard += "--- " + entry.getKey() + ": " + entry.getValue() + " ---\n";
        }
        return countriesLeaderBoard;
    }
    public String generateCountryLeaderBoard(Long tournament_id, Long country_id) {
        List<Rewards> allScoresOfTheTournament = rewardsService.getScoresByTournament(tournament_id);
        Country country = findCountryById(country_id);

        Long total_score_country = 0L;
        Map<String, Long> playersToScoreMap = new HashMap<String, Long>();
        for(Rewards currentScore : allScoresOfTheTournament) {
            Player currentPlayer = currentScore.getPlayer();
            if(Objects.equals(country.getName(), currentPlayer.getCountry())) {
                total_score_country += currentScore.getScore();
                String player_info = "user name: " + currentPlayer.getUserName() + ", player id: " + currentPlayer.getId();
                playersToScoreMap.put(player_info, currentScore.getScore());
            }
        }
        Map<String, Long> sorted = sortMapByValue(playersToScoreMap);
        String countryLeaderBoard = "Total score of " + country.getName() + " and its players sorted by the score:\n";
        for(Map.Entry<String, Long> entry : sorted.entrySet()) {
            countryLeaderBoard += "--- " + entry.getKey() + ": " + entry.getValue() + " ---\n";
        }
        return countryLeaderBoard;
    }
    // function to sort hashmap by values
    private Map<String, Long> sortMapByValue(Map<String, Long> map){
        // Create a list of map entries
        List<Map.Entry<String, Long>> list = new ArrayList<> (map.entrySet ());

        // Sort the list using a lambda expression
        list.sort(Map.Entry.<String, Long>comparingByValue().reversed());

        // Create a linked hash map to preserve the order
        Map<String, Long> sortedMap = new LinkedHashMap<> ();

        // Iterate over the sorted list and put the entries to the sorted map
        for (Map.Entry<String, Long> entry : list) {
            sortedMap.put (entry.getKey (), entry.getValue ());
        }

        // Return the sorted map
        return sortedMap;
    }
}
