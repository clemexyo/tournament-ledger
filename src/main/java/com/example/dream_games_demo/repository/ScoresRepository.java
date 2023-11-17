package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Scores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoresRepository extends JpaRepository<Scores, Long> {
    @Query("SELECT r " +
            "FROM Scores r " +
            "WHERE r.tournament_group.id = :tournamentGroupId")
    Optional<List<Scores>> getAllScoresOfTournament(@Param("tournamentGroupId") Long tournamentGroupId);

    @Query("SELECT r FROM Scores r WHERE r.tournament_group.id = :tournamentGroupId AND r.player.id = :playerId")
    Optional<Scores> findByPlayerAndTournamentGroup(@Param("playerId") Long playerId, @Param("tournamentGroupId") Long tournamentGroupId);

    @Query("SELECT r FROM Scores r WHERE r.tournament.id = :tournament_id")
    Optional<List<Scores>> findByTournament(@Param("tournament_id") Long tournament_id);
}
