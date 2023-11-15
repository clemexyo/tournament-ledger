package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.UnableToAddPlayerToGroupException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Rewards;
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
    private RewardsService rewardsService;

    public Optional<List<TournamentGroup>> findPendingTournamentGroups(){
        return tournamentGroupsRepository.findPendingTournamentGroups();
    }
    public TournamentGroup createGroupAndAssignPlayer(Player player, Tournament latest_tournament){
        TournamentGroup to_return = null;
        try{
            TournamentGroup tournamentGroup = new TournamentGroup(player, latest_tournament);
            //playerService.playerEnteredGroup(player.getId());
            player.setCan_enter(false);
            tournamentGroupsRepository.save(tournamentGroup);
            rewardsService.createReward(player, latest_tournament, tournamentGroup);
            to_return = tournamentGroup;
        }
        catch (IllegalStateException e){
            //for some reason the player could not be added to the newly created tournament group instance
            //this is very, very bad probably should look into it. The flow of the program should never
            //be coming to here as previous checks are sufficient.
            throw new UnableToAddPlayerToGroupException();
        }
        return to_return;
    }

    public TournamentGroup assignPlayerToAvailableGroup(Player player, List<TournamentGroup> pendingTournamentGroups, Tournament latest_tournament){
        TournamentGroup to_return = null;
        boolean placed = false;
        for (TournamentGroup currentGroup: pendingTournamentGroups){
            if(uniqueCountry(player, currentGroup)){
                try {
                    addPlayer(player, currentGroup);
                    if(currentGroup.isReadyToStart()){
                        currentGroup.setIsActive(true);
                        tournamentGroupsRepository.save(currentGroup);
                    }
                    rewardsService.createReward(player, latest_tournament, currentGroup);
                    to_return = currentGroup;
                    placed = true;
                    break;
                }
                catch (IllegalStateException e){
                    //No empty column available to assign the player.
                    //this is very, very bad. We should never come here as previous checks should be sufficient.
                    throw new UnableToAddPlayerToGroupException();
                }
            }
        }
        if(!placed){
            //in this case, the pending tournament groups may have an empty spot
            //however the player that is asking to join is not from a unique country to any group
            //therefore create a new tournament group instance and assign the player there.
            to_return = createGroupAndAssignPlayer(player, latest_tournament);
            placed = true;
        }
        return to_return;
    }
    public String generateLeaderBoard(TournamentGroup tournamentGroup){
        List<Rewards> playersOrderedByGroupScore = rewardsService.orderedPlayers(tournamentGroup);
        StringBuilder leaderBoard = new StringBuilder();

        if(!tournamentGroup.isReadyToStart()){
            leaderBoard.append("The group is not yet ready to start. Waiting for other players to join\n");
        }
        leaderBoard.append("Players from highest score to lowest:\n");
        for(Rewards currentInstance: playersOrderedByGroupScore){
            Long player_id = currentInstance.getPlayer().getId();
            String player_name = currentInstance.getPlayer().getUserName();
            String player_country = currentInstance.getPlayer().getCountry();
            Long current_score = currentInstance.getScore();

            leaderBoard.append("--- ").append(player_id).append(", ").append(player_name)
                    .append(", ").append(player_country).append(", ")
                    .append(current_score).append(" ---\n");
        }
        return leaderBoard.toString();
    }
    public Optional<TournamentGroup> isPlayerInActiveGroup(Long player_id){
        return tournamentGroupsRepository.findTournamentGroupByPlayerId(player_id);
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
       //playerService.playerEnteredGroup(player.getId());
        player.setCan_enter(false);
    }

}
