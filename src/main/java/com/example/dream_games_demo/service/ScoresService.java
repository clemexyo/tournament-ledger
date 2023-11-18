package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.NoScoresFoundException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Scores;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.ScoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class ScoresService {
    @Autowired
    private ScoresRepository scoresRepository;
    public void createScore(Player player, Tournament tournament, TournamentGroup tournamentGroup){
        Scores score = new Scores(player, tournament, tournamentGroup);
        scoresRepository.save(score);
    }
    public List<Scores> getAllScoresOfTournament(Long tournament_id){
        Optional<List<Scores>> optionalScores = scoresRepository.getAllScoresOfTournamentGroup(tournament_id);
        if(!optionalScores.isPresent()) {
            throw new NoScoresFoundException();
        }
        return optionalScores.get();
    }
    public List<Scores> orderedScoresByPlayerScore(TournamentGroup tournamentGroup){
        List<Scores> scoresOfTournament = getAllScoresOfTournament(tournamentGroup.getId());

        // Order the list based on score and then latest_update
        scoresOfTournament.sort(
                Comparator.comparing(Scores::getScore, Comparator.reverseOrder())
                        .thenComparing(Scores::getLatestUpdate, Comparator.reverseOrder())
        );
        return scoresOfTournament;
    }
    public void incrementPlayerScore(Long player_id, TournamentGroup tournamentGroup){
        Scores score = findByPlayerAndTournamentGroup(player_id, tournamentGroup.getId());

        score.incrementPlayerScore();
        score.setLatestUpdateToNow();
        scoresRepository.save(score);

        Long total_group_score = tournamentGroup.getTotal_group_score();
        total_group_score += 1;
        tournamentGroup.setTotal_group_score(total_group_score);
    }
    private Scores findByPlayerAndTournamentGroup(Long player_id, Long tournament_group_id) {
        Optional<Scores> optionalScores = scoresRepository.findByPlayerAndTournamentGroup(player_id, tournament_group_id);
        if(optionalScores.isEmpty()) {
            throw new NoScoresFoundException();
        }
        return optionalScores.get();
    }
    public List<Scores> getScoresByTournament(Long tournament_id){
        Optional<List<Scores>> optionalScores = scoresRepository.findByTournament(tournament_id);
        if(optionalScores.get().isEmpty()){
            throw new NoScoresFoundException();
        }
        return optionalScores.get();
    }
}
