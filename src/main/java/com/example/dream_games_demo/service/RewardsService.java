package com.example.dream_games_demo.service;

import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.RewardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardsService {
    @Autowired
    private RewardsRepository rewardsRepository;
    public void createReward(Player player, Tournament tournament, TournamentGroup tournamentGroup){
        Rewards reward = new Rewards(player, tournament, tournamentGroup);
        rewardsRepository.save(reward);
    }
    public List<Rewards> orderedPlayers(TournamentGroup tournamentGroup){
        List<Rewards> allRewardsOfTheGroup = rewardsRepository.findAllByTournamentGroup(tournamentGroup.getId());
        return allRewardsOfTheGroup.stream()
                .sorted(Comparator.comparing(Rewards::getScore).reversed())
                .collect(Collectors.toList());
    }
}
