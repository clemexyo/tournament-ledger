package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.NoTournamentGroupFoundException;
import com.example.dream_games_demo.exceptions.RewardNotFoundException;
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
            player.payToEnter();
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
                    player.payToEnter();
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
        List<Rewards> rewardsOrderedByPlayerScore = rewardsService.orderedRewardsByPlayerScore(tournamentGroup);
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
                playersLeaveTheGroup(currentGroup);
            }
        }
        else {
            throw new NoTournamentGroupFoundException();
        }
    }
    public String getPlayerGroupRank(Long player_id, Long tournament_group_id){
        TournamentGroup tournamentGroup = findById(tournament_group_id);
        List<Rewards> temp = rewardsService.orderedRewardsByPlayerScore(tournamentGroup);

        int rank = 0;
        for(int i = 0; i < temp.size(); i++){
            if(temp.get(i).getPlayer().getId() == player_id){
                rank = i + 1;
            }
        }
        if(rank == 0){
            throw new NoTournamentGroupFoundException();
        }
        else {
            return "Player's rank in tournament group: " + tournament_group_id + " ,is " + rank;
        }
    }
    public List<TournamentGroup> findAllGroupsByTournament(Long tournament_id){
        Optional<List<TournamentGroup>> optionalTournamentGroups = tournamentGroupsRepository.findAllGroupsByTournament(tournament_id);
        if(optionalTournamentGroups.get().isEmpty()){
            throw new NoTournamentGroupFoundException();
        }
        return optionalTournamentGroups.get();
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
    private void playersLeaveTheGroup(TournamentGroup tournamentGroup){
        List<Rewards> rewardsOfTheGroupOrderedByPlayerScore = rewardsService.orderedRewardsByPlayerScore(tournamentGroup);

        if(tournamentGroup.isFull()){
            //all players except winner and second will leave.
            //winner and the second have to claim their rewards
            //in order to leave the group
            //here we're not adding the prizes of winner and second because they need to
            //send a ClaimReward request to get them.
            tournamentGroup.setWinner(rewardsOfTheGroupOrderedByPlayerScore.get(0).getPlayer());
            tournamentGroup.setSecond(rewardsOfTheGroupOrderedByPlayerScore.get(1).getPlayer());
            for(int i = 2; i < rewardsOfTheGroupOrderedByPlayerScore.size(); i++){
                rewardsOfTheGroupOrderedByPlayerScore.get(i).getPlayer().setCan_enter(true);
                playerRepository.save(rewardsOfTheGroupOrderedByPlayerScore.get(i).getPlayer());
            }
        }
        else {
            //this means tournament never began in the first place, all remaining player will leave.
            //here players will also get their 1000 coins back since they never got to start playing
            for(Rewards currentReward: rewardsOfTheGroupOrderedByPlayerScore){
                Player currentPlayer = currentReward.getPlayer();
                currentPlayer.setCan_enter(true);
                currentPlayer.getPaymentBack();
                playerRepository.save(currentReward.getPlayer());
            }
        }
    }
    public TournamentGroup findById(Long id){
        Optional<TournamentGroup> optionalTournamentGroup = tournamentGroupsRepository.findById(id);
        if(optionalTournamentGroup.isPresent()) {
            return optionalTournamentGroup.get();
        }
        else {
            throw new NoTournamentGroupFoundException();
        }
    }
    public TournamentGroup findLastGroupOfPlayer(Long player_id){
        Optional<TournamentGroup> optionalTournamentGroup = tournamentGroupsRepository.findLastGroupOfPlayer(player_id);
        if(optionalTournamentGroup.isPresent()){
            return optionalTournamentGroup.get();
        }
        else {
            throw new RewardNotFoundException();
        }
    }
}
