package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.NoPlayerFoundException;
import com.example.dream_games_demo.exceptions.NoRewardsFoundException;
import com.example.dream_games_demo.exceptions.NoScoreFoundException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.RewardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RewardsService {
    @Autowired
    private RewardsRepository rewardsRepository;
    public void createReward(Player player, Tournament tournament, TournamentGroup tournamentGroup){
        Rewards reward = new Rewards(player, tournament, tournamentGroup);
        rewardsRepository.save(reward);
    }
    public List<Rewards> orderedRewardsByPlayerScore(TournamentGroup tournamentGroup){
        Optional<List<Rewards>> optionalGroupRewardsOrderedByPlayerScore = rewardsRepository.groupRewardsOrderedByPlayerScore(tournamentGroup.getId());
        if(!optionalGroupRewardsOrderedByPlayerScore.get().isEmpty()){
            return optionalGroupRewardsOrderedByPlayerScore.get();
        }
        else {
            throw new NoRewardsFoundException();
        }
    }
    public void incrementPlayerScore(Long player_id, TournamentGroup tournamentGroup){
        Optional<Rewards> optionalReward = rewardsRepository.findByPlayerAndTournamentGroup(player_id, tournamentGroup.getId());
        if(optionalReward.isPresent()){
            Long player_score = optionalReward.get().getScore();
            player_score += 1;
            optionalReward.get().setScore(player_score);

            Long total_group_score = tournamentGroup.getTotal_group_score();
            total_group_score += 1;
            tournamentGroup.setTotal_group_score(total_group_score);
        }
        else {
            throw new NoRewardsFoundException();
        }
    }
    public List<Rewards> getScoresByTournament(Long tournament_id){
        Optional<List<Rewards>> optionalRewards = rewardsRepository.findByTournament(tournament_id);
        if(optionalRewards.get().isEmpty()){
            throw new NoScoreFoundException();
        }
        return optionalRewards.get();
    }
}
