package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.NoTournamentGroupFoundException;
import com.example.dream_games_demo.exceptions.UnableToAddPlayerToGroupException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.PlayerRepository;
import com.example.dream_games_demo.repository.TournamentGroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentGroupService {
    @Autowired
    private TournamentGroupsRepository tournamentGroupsRepository;
    @Autowired
    private RewardsService rewardsService;
    @Autowired
    private PlayerRepository playerRepository;

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
                    if(currentGroup.isFull()){
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
    public String generateLeaderBoard(TournamentGroup tournamentGroup, Long tournament_id){
        List<Rewards> rewardsOrderedByPlayerScore = rewardsService.orderedRewardsByPlayerScore(tournamentGroup, tournament_id);
        StringBuilder leaderBoard = new StringBuilder();

        if(!tournamentGroup.isFull()){
            leaderBoard.append("The group is not full.\n");
        }
        leaderBoard.append("Players from highest score to lowest:\n");
        for(Rewards currentReward: rewardsOrderedByPlayerScore){
            Long player_id = currentReward.getPlayer().getId();
            String player_name = currentReward.getPlayer().getUserName();
            String player_country = currentReward.getPlayer().getCountry();
            Long current_score = currentReward.getScore();

            leaderBoard.append("--- ").append(player_id).append(", ").append(player_name)
                    .append(", ").append(player_country).append(", ")
                    .append(current_score).append(" ---\n");
        }
        return leaderBoard.toString();
    }
    public Optional<TournamentGroup> isPlayerInActiveGroup(Long player_id){
        return tournamentGroupsRepository.findTournamentGroupByPlayerId(player_id);
    }
    public void endTournamentGroups(Long tournament_id){
        Optional<List<TournamentGroup>> optionalGroups = tournamentGroupsRepository.findAllGroupsByTournament(tournament_id);
        if(!optionalGroups.get().isEmpty()){
            List<TournamentGroup> tournamentGroups = optionalGroups.get();
            for(TournamentGroup currentGroup: tournamentGroups){
                currentGroup.setIsActive(false);
                tournamentGroupsRepository.save(currentGroup);
                playersLeaveTheGroup(currentGroup, tournament_id);
            }
        }
        else {
            throw new NoTournamentGroupFoundException();
        }
    }
    private Boolean uniqueCountry(Player player, TournamentGroup currentGroup){
        String newPlayerCountry = player.getCountry();
        List<String> counties = currentGroup.tournamentPlayerCountries();

        boolean unique = true;
        for(String currentCountry: counties){
            if(currentCountry.equals(newPlayerCountry)){
                unique = false;
                break;
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
    private void playersLeaveTheGroup(TournamentGroup tournamentGroup, Long tournament_id){
        List<Rewards> rewardsOfTheGroupOrderedByPlayerScore = rewardsService.orderedRewardsByPlayerScore(tournamentGroup, tournament_id);

        if(tournamentGroup.isFull()){
            //all players except winner and second will leave.
            //winner and the second have to claim their rewards
            //in order to leave the group
            tournamentGroup.setWinner(rewardsOfTheGroupOrderedByPlayerScore.get(0).getPlayer());
            for(int i = 2; i < rewardsOfTheGroupOrderedByPlayerScore.size(); i++){
                rewardsOfTheGroupOrderedByPlayerScore.get(i).getPlayer().setCan_enter(true);
                playerRepository.save(rewardsOfTheGroupOrderedByPlayerScore.get(i).getPlayer());
            }
        }
        else {
            //this means tournament never began in the first place, all remaining player will leave.
            for(Rewards currentReward: rewardsOfTheGroupOrderedByPlayerScore){
                currentReward.getPlayer().setCan_enter(true);
                playerRepository.save(currentReward.getPlayer());
            }
        }
    }
}
