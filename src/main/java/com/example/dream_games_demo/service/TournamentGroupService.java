package com.example.dream_games_demo.service;

import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.PlayerRepository;
import com.example.dream_games_demo.repository.TournamentGroupsRepository;
import com.example.dream_games_demo.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TournamentGroupService {
    @Autowired
    private TournamentGroupsRepository tournamentGroupsRepository;
    @Autowired
    private PlayerService playerService;

    public Optional<List<TournamentGroup>> findPendingTournamentGroups(){
        return tournamentGroupsRepository.findPendingTournamentGroups();
    }
    public Map<String, Object> createGroupAndAssignPlayer(Player player, Tournament latest_tournament){
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;
        try{
            //Tournament tournament = tournamentService.findLatestTournament();
            TournamentGroup tournamentGroup = new TournamentGroup(player, latest_tournament);
            playerService.playerEnteredGroup(player.getId());
            tournamentGroupsRepository.save(tournamentGroup);
            message = "Player assigned to a new tournament group. Currently waiting for other players to join.\n" +
                    "Current status of the board: ";


            httpStatus = HttpStatus.OK;
        }
        catch (IllegalStateException e){
            message = "this is very very bad";
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("httpStatus", httpStatus);
        return map;
    }

    public Map<String, Object> assignPlayerToAvailableGroup(Player player, List<TournamentGroup> pendingTournamentGroups, Tournament latest_tournament){
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;

        for (TournamentGroup currentGroup: pendingTournamentGroups){
            if(uniqueCountry(player, currentGroup)){
                try {
                    addPlayer(player, currentGroup);
                    if(currentGroup.isReadyToStart()){
                        currentGroup.setIsActive(true);
                        tournamentGroupsRepository.save(currentGroup);
                    }
                }
                catch (IllegalStateException e){
                    message = "this is very very bad";
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
            else{
                Map<String, Object> result = createGroupAndAssignPlayer(player, latest_tournament);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("httpStatus", httpStatus);
        return map;
    }
    private Boolean uniqueCountry(Player player, TournamentGroup currentGroup){
        String newPlayerCountry = player.getCountry();
        List<String> counties = currentGroup.tournamentPlayerCountries();

        boolean unique = true;
        for(String currentCountry: counties){
            if(currentCountry.equals(newPlayerCountry)){
                unique = false;
            }
        }
        return unique;
    }
    private void addPlayer(Player player, TournamentGroup currentGroup){
       currentGroup.addPlayer(player);
       tournamentGroupsRepository.save(currentGroup);
       playerService.playerEnteredGroup(player.getId());
    }
}
