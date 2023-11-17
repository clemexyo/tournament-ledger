package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.CountryNotFoundException;
import com.example.dream_games_demo.exceptions.CreateCountryException;
import com.example.dream_games_demo.exceptions.NoCountryFoundException;
import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Scores;
import com.example.dream_games_demo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ScoresService scoresService;
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
        List<Scores> allScoresOfTheTournament = scoresService.getScoresByTournament(tournament_id);
        List<Country> allCountries = allCountries();

        //now sort the scores of the tournament with respect to latest_update ascending order,
        //this way we will have the latest updated score as the last element of the list.
        //this information of the latest update will be useful if we encounter countries that have the same total score.
        //the country that has the higher latest_update value will be higher in the leaderboard.
        allScoresOfTheTournament.sort(Comparator.comparing(Scores::getLatestUpdate));

        Map<String, String> CountryToScoreMap = new HashMap<>();
        for(Country currentCountry : allCountries){
            Long total_score_of_current_country = 0L;
            String latest_update = "";
            for(Scores currentScore : allScoresOfTheTournament){
                if(Objects.equals(currentCountry.getName(), currentScore.getPlayer().getCountry())) {
                    total_score_of_current_country += currentScore.getScore();
                    latest_update = currentScore.getLatestUpdate().toString();
                }
            }
            CountryToScoreMap.put(currentCountry.getName(),
                            (total_score_of_current_country.toString() + "%" + latest_update));
        }
        Map<String, String> sortedByScore = sortByValues(CountryToScoreMap);
        String countriesLeaderBoard = "Countries from highest score to lowest:\n" +
                "(in case of countries with equal scores, they are sorted with respect to latest score achievement)\n";
        for(Map.Entry<String, String> entry : sortedByScore.entrySet()) {
            countriesLeaderBoard += "--- " + entry.getKey() + ": " + entry.getValue().split("%")[0] + " ---\n";
        }
        return countriesLeaderBoard;
    }
    public String generateCountryLeaderBoard(Long tournament_id, Long country_id) {
        List<Scores> allScoresOfTheTournament = scoresService.getScoresByTournament(tournament_id);
        Country country = findCountryById(country_id);

        Long total_score_country = 0L;
        Map<String, String> playersToScoreMap = new HashMap<String, String>();
        for(Scores currentScore : allScoresOfTheTournament) {
            Player currentPlayer = currentScore.getPlayer();
            if(Objects.equals(country.getName(), currentPlayer.getCountry())) {
                total_score_country += currentScore.getScore();
                String player_info = "user name: " + currentPlayer.getUserName() + ", player id: " + currentPlayer.getId();
                String player_update_time = currentScore.getLatestUpdate().toString();
                playersToScoreMap.put(player_info,
                        (currentScore.getScore().toString() + "%" + player_update_time));
            }
        }
        Map<String, String> sorted = sortByValues(playersToScoreMap);
        String countryLeaderBoard = "Total score of " + country.getName() + " is " +
                total_score_country + " and its players sorted by the score:\n" +
                "(in case of players with equal scores, they are sorted with respect to latest score achievement.)\n";
        for(Map.Entry<String, String> entry : sorted.entrySet()) {
            countryLeaderBoard += "--- " + entry.getKey() + ": " + entry.getValue().split("%")[0] + " ---\n";
        }
        return countryLeaderBoard;
    }
    // function to sort hashmap by values
    private static Map<String, String> sortByValues(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, String>comparingByValue(MapValueComparator::compareValues))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private static class MapValueComparator {
        private static int compareValues(String value1, String value2) {
            String[] parts1 = value1.split("%");
            String[] parts2 = value2.split("%");

            // Parse Long and LocalDateTime components
            Long longValue1 = Long.parseLong(parts1[0]);
            LocalDateTime dateTimeValue1 = LocalDateTime.parse(parts1[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Long longValue2 = Long.parseLong(parts2[0]);
            LocalDateTime dateTimeValue2 = LocalDateTime.parse(parts2[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Compare first based on Long (descending), then on LocalDateTime (descending)
            int compareResult = Long.compare(longValue2, longValue1);
            if (compareResult == 0) {
                return dateTimeValue2.compareTo(dateTimeValue1);
            }
            return compareResult;
        }
    }
}
