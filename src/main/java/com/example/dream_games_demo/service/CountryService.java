package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.CreateCountryException;
import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.model.TournamentGroup;
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
        return countryRepository.findAll();
    }
    public String generateCountriesLeaderBoard(Long tournament_id){
        List<Rewards> allScoresOfTheTournament = rewardsService.getScoresByTournament(tournament_id);
        List<Country> allCountries = countryRepository.findAll();

        Map<String, Long> CountryToScoreMap = new HashMap<String, Long>();
        for(Country currentCountry : allCountries){
            Long total_score_of_current_country = 0L;
            for(Rewards currentScore : allScoresOfTheTournament){
                if(currentCountry.getName() == currentScore.getPlayer().getCountry()) {
                    total_score_of_current_country += currentScore.getScore();
                }
            }
            CountryToScoreMap.put(currentCountry.getName(), total_score_of_current_country);
        }
        Map<String, Long> sortedByScore = sortByValue(CountryToScoreMap);
        String countriesLeaderBoard = "Countries from highest score to lowest:\n";
        for(Map.Entry<String, Long> entry : CountryToScoreMap.entrySet()) {
            countriesLeaderBoard += "--- " + entry.getKey() + ": " + entry.getValue() + " ---\n";
        }
        return countriesLeaderBoard;
    }
    // function to sort hashmap by values
    private Map<String, Long> sortByValue(Map<String, Long> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Long> > list
                = new LinkedList<Map.Entry<String, Long> >(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                (i1,
                 i2) -> i1.getValue().compareTo(i2.getValue()));

        // put data from sorted list to hashmap
        Map<String, Long> temp
                = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
