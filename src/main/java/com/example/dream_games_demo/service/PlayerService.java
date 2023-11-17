package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.*;
import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
        if(playersObjectList.isEmpty()){
            throw new NoPlayerFoundException();
        }
        List<String> playersStringList = new ArrayList<String>();
        for (Player currentPlayer : playersObjectList) {
            playersStringList.add(currentPlayer.toString());
        }
        return playersStringList;
    }
    public Player findPlayerById(Long id){
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if(optionalPlayer.isPresent()){
            return optionalPlayer.get();
        }
        else{
            throw new PlayerNotFoundException();
        }
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
        Player player = findPlayerById(id);
        try{
            TournamentGroup playerInActiveGroup = tournamentGroupService.isPlayerInActiveGroup(player.getId());
            Long level = player.getLevel();
            Long coins = player.getCoins();

            player.setCoins(coins + 25);
            player.setLevel(level + 1);

            if(playerInActiveGroup != null){
                rewardsService.incrementPlayerScore(player.getId(), playerInActiveGroup);
            }
            playerRepository.save(player);
            return player.toString();
        }catch (Exception e){
            throw new UpdatePlayerLevelRequestException();
        }
    }
    public boolean playerStatus(Long id){
        boolean can_enter = false;
        Player player = findPlayerById(id);
        if(player.getCan_enter() && player.getCoins() >= 1000 && player.getLevel() >= 20){
            can_enter = true;
        }
        else {
            throw new PlayerCannotEnterTournamentException();
        }
        return can_enter;
    }
    public void claimReward(Player player){
        TournamentGroup tournamentGroup = tournamentGroupService.findLastGroupOfPlayer(player.getId());
        if(Objects.equals(player.getId(), tournamentGroup.getWinner().getId())){
            player.getWinnerPrize();
        }
        else if(Objects.equals(player.getId(), tournamentGroup.getSecond().getId())){
            player.getSecondPrize();
        }
        else{
            throw new RewardNotFoundException();
        }
    }
}
