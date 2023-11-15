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
    private PlayerService playerService;
    @Autowired
    private RewardsService rewardsService;

    public Optional<List<TournamentGroup>> findPendingTournamentGroups(){
        return tournamentGroupsRepository.findPendingTournamentGroups();
    }
    public String createGroupAndAssignPlayer(Player player, Tournament latest_tournament){
        String leaderBoard = "";
        try{
            TournamentGroup tournamentGroup = new TournamentGroup(player, latest_tournament);
            playerService.playerEnteredGroup(player.getId());
            tournamentGroupsRepository.save(tournamentGroup);
            leaderBoard = "Player assigned to a new tournament group. Currently waiting for other players to join.\n" +
                    "Current status of the board:\n";
            rewardsService.createReward(player, latest_tournament, tournamentGroup);
            leaderBoard += generateLeaderBoard(tournamentGroup);
        }
        catch (IllegalStateException e){
            //for some reason the player could not be added to the newly created tournament group instance
            //this is very, very bad probably should look into it. The flow of the program should never
            //be coming to here as previous checks are sufficient.
            throw new UnableToAddPlayerToGroupException();
        }
        return leaderBoard;
    }

    public String assignPlayerToAvailableGroup(Player player, List<TournamentGroup> pendingTournamentGroups, Tournament latest_tournament){
        String leaderBoard = "";
        for (TournamentGroup currentGroup: pendingTournamentGroups){
            if(uniqueCountry(player, currentGroup)){
                try {
                    addPlayer(player, currentGroup);
                    if(currentGroup.isReadyToStart()){
                        currentGroup.setIsActive(true);
                        leaderBoard = "The group is ready to start the game!\n";
                    }
                    else{
                        leaderBoard = "The group is not yet ready to start.\nCurrent board:\n" + generateLeaderBoard(currentGroup);
                    }
                    tournamentGroupsRepository.save(currentGroup);
                    rewardsService.createReward(player, latest_tournament, currentGroup);
                    leaderBoard = generateLeaderBoard(currentGroup);
                    break;
                }
                catch (IllegalStateException e){
                    //No empty column available to assign the player.
                    //this is very, very bad. We should never come here as previous checks should be sufficient.
                    throw new UnableToAddPlayerToGroupException();
                }
            }
            else{
                //in this case, the tournament group does have an empty spot
                //however the player that is asking to join is not from a unique country
                //therefore create a new tournament group instance and assign the player there.
                leaderBoard = createGroupAndAssignPlayer(player, latest_tournament);
            }
        }
        return leaderBoard;
    }
    public String generateLeaderBoard(TournamentGroup tournamentGroup){
        List<Rewards> playersOrderedByGroupScore = rewardsService.orderedPlayers(tournamentGroup);
        StringBuilder leaderBoard = new StringBuilder("Players from highest score to lowest:\n");

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
