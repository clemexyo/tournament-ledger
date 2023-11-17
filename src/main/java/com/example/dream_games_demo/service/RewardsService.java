package com.example.dream_games_demo.service;

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


@Service
public class RewardsService {
    @Autowired
    private RewardsRepository rewardsRepository;
    public void createReward(Player player, Tournament tournament, TournamentGroup tournamentGroup){
        Rewards reward = new Rewards(player, tournament, tournamentGroup);
        rewardsRepository.save(reward);
    }
    public List<Rewards> getAllScoresOfTournament(Long tournament_id){
        Optional<List<Rewards>> optionalRewards = rewardsRepository.getAllScoresOfTournament(tournament_id);
        if(!optionalRewards.isPresent()) {
            throw new NoRewardsFoundException();
        }
        return optionalRewards.get();
    }
    public List<Rewards> orderedRewardsByPlayerScore(TournamentGroup tournamentGroup){
        List<Rewards> rewardsOfTournament = getAllScoresOfTournament(tournamentGroup.getId());

        // Order the list based on score and then latest_update
        rewardsOfTournament.sort(
                Comparator.comparing(Rewards::getScore, Comparator.reverseOrder())
                        .thenComparing(Rewards::getLatestUpdate, Comparator.reverseOrder())
        );
        return rewardsOfTournament;
    }
    public void incrementPlayerScore(Long player_id, TournamentGroup tournamentGroup){
        Rewards reward = findByPlayerAndTournamentGroup(player_id, tournamentGroup.getId());

        reward.incrementPlayerScore();
        reward.setLatestUpdateToNow();
        rewardsRepository.save(reward);

        Long total_group_score = tournamentGroup.getTotal_group_score();
        total_group_score += 1;
        tournamentGroup.setTotal_group_score(total_group_score);
    }
    private Rewards findByPlayerAndTournamentGroup(Long player_id, Long tournament_group_id) {
        Optional<Rewards> optionalRewards = rewardsRepository.findByPlayerAndTournamentGroup(player_id, tournament_group_id);
        if(optionalRewards.isEmpty()) {
            throw new NoRewardsFoundException();
        }
        return optionalRewards.get();
    }
    public List<Rewards> getScoresByTournament(Long tournament_id){
        Optional<List<Rewards>> optionalRewards = rewardsRepository.findByTournament(tournament_id);
        if(optionalRewards.get().isEmpty()){
            throw new NoScoreFoundException();
        }
        return optionalRewards.get();
    }
}
