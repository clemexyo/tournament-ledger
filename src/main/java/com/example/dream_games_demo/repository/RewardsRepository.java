package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.model.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, Long> {
    @Query("SELECT r " +
            "FROM Rewards r " +
            "WHERE r.tournament_group.id = :tournamentGroupId " +
            "ORDER BY r.player_score DESC")
    Optional<List<Rewards>> groupRewardsOrderedByPlayerScore(@Param("tournamentGroupId") Long tournamentGroupId);

    @Query("SELECT r FROM Rewards r WHERE r.tournament_group.id = :tournamentGroupId AND r.player.id = :playerId")
    Optional<Rewards> findByPlayerAndTournamentGroup(@Param("playerId") Long playerId, @Param("tournamentGroupId") Long tournamentGroupId);

    @Query("SELECT r FROM Rewards r WHERE r.tournament.id = :tournament_id")
    Optional<List<Rewards>> findByTournament(@Param("tournament_id") Long tournament_id);
}
