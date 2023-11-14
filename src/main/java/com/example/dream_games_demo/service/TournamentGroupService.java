package com.example.dream_games_demo.service;

import com.example.dream_games_demo.model.Player;
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
    public Map<String, Object> createGroupAndAssignPlayer(Player player){
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;
        try{
            TournamentGroup tournamentGroup = new TournamentGroup(player);
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

    public Map<String, Object> assignPlayerToAvailableGroup(Player player, List<TournamentGroup> pendingTournamentGroups){
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;

        Boolean placed = false;
        for (int i = 0; i < pendingTournamentGroups.size(); i++){
            TournamentGroup currentGroup = pendingTournamentGroups.get(i);
            if(!notUniqueCountry(player, currentGroup)){
                placed = true;
                try {
                    addPlayer(player, currentGroup);

                }
                catch (IllegalStateException e){
                    message = "this is very very bad";
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("httpStatus", httpStatus);
        return map;
    }
    private Boolean notUniqueCountry(Player player, TournamentGroup currentGroup){
        String newPlayerCountry = player.getCountry();
        Player player1 = currentGroup.getPlayer1();
        Player player2 = currentGroup.getPlayer2();
        Player player3 = currentGroup.getPlayer3();
        Player player4 = currentGroup.getPlayer4();
        Player player5 = currentGroup.getPlayer5();

        String c1 = player1 == null ? "" : player1.getCountry();
        String c2 = player2 == null ? "" : player2.getCountry();
        String c3 = player3 == null ? "" : player3.getCountry();
        String c4 = player4 == null ? "" : player4.getCountry();
        String c5 = player5 == null ? "" : player5.getCountry();

        return newPlayerCountry == c1 ||
                newPlayerCountry == c2 ||
                newPlayerCountry == c3 ||
                newPlayerCountry == c4 ||
                newPlayerCountry == c5;
    }
    private void addPlayer(Player player, TournamentGroup currentGroup){
        Player player1 = currentGroup.getPlayer1();
        Player player2 = currentGroup.getPlayer2();
        Player player3 = currentGroup.getPlayer3();
        Player player4 = currentGroup.getPlayer4();
        Player player5 = currentGroup.getPlayer5();

        if(player1 == null){
            currentGroup.setPlayer1(player);
        }
        else if(player2 == null){
            currentGroup.setPlayer2(player);
        }
        else if(player3 == null){
            currentGroup.setPlayer3(player);
        }
        else if(player4 == null){
            currentGroup.setPlayer4(player);
        }
        else if(player5 == null){
            currentGroup.setPlayer5(player);
        }
        else {
            //we will hope for the best and the controllers added before this piece of code will be
            //enough that, so we will never end up in this exception.
            throw new IllegalStateException("No empty column available to assign the player.");
        }
        tournamentGroupsRepository.save(currentGroup);
    }
}
