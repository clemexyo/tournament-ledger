package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.*;
import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.CountryRepository;
import com.example.dream_games_demo.repository.PlayerRepository;
import com.example.dream_games_demo.repository.TournamentGroupsRepository;
import com.example.dream_games_demo.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CountryService countryService;
    @Autowired
    private TournamentGroupService tournamentGroupService;
    @Autowired
    private RewardsService rewardsService;

    public List<String> allPlayers(){
        List<Player> playersObjectList = playerRepository.findAll();
        List<String> playersStringList = new ArrayList<String>();
        for (Player currentPlayer : playersObjectList) {
            playersStringList.add(currentPlayer.toString());
        }
        return playersStringList;
    }

    public Optional<Player> findPlayerById(Long id){
        return playerRepository.findById(id);

    }

    public String createPlayer(String user_name){
        try{
            Player player = new Player(user_name);
            playerRepository.save(player);

            List<Country> allCountries = countryService.allCountries();
            Random random = new Random();
            Country randomCountry = allCountries.get(random.nextInt(allCountries.size()));

            player.setCountry(randomCountry);
            playerRepository.save(player);

            return player.toString();
        }catch (Exception e){
            throw new CreatePlayerException();
        }

    }

    public String updateLevel(Long id){
        Optional<Player> optional = playerRepository.findById(id);
        if(optional.isPresent()) {
            try{
                Player player = optional.get();
                Optional<TournamentGroup> playerInActiveGroup = tournamentGroupService.isPlayerInActiveGroup(player.getId());

                Long level = player.getLevel();
                Long coins = player.getCoins();

                player.setCoins(coins + 25);
                player.setLevel(level + 1);
                if(playerInActiveGroup.isPresent()){
                    rewardsService.incrementPlayerScore(player.getId(), playerInActiveGroup.get());
                }
                playerRepository.save(player);
                return player.toString();
            }catch (Exception e){
                throw new UpdatePlayerLevelRequestException();
            }
        }
        else{
            throw new PlayerNotFoundException();
        }
    }

    public boolean playerStatus(Long id){
        boolean can_enter = false;
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            if(player.getCan_enter() && player.getCoins() >= 1000 && player.getLevel() >= 20){
                can_enter = true;
            }
            else {
                throw new PlayerCannotEnterTournamentException();
            }
        }
        else{
           throw new PlayerNotFoundException();
        }
        return can_enter;
    }
}
