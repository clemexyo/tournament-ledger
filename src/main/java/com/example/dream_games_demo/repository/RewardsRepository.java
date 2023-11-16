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
    @Query("SELECT r FROM Rewards r WHERE r.tournament_group.id = :tournamentGroupId")
    List<Rewards> findAllByTournamentGroup(@Param("tournamentGroupId") Long tournamentGroupId);

    @Query("SELECT r FROM Rewards r WHERE r.tournament_group.id = :tournamentGroupId AND r.player.id = :playerId")
    Optional<Rewards> findByPlayerAndTournamentGroup(@Param("playerId") Long playerId, @Param("tournamentGroupId") Long tournamentGroupId);


    @Query("SELECT r.player FROM Rewards r WHERE r.tournament_group.id = :tournamentGroupId " +
            "AND r.tournament.id = :tournamentId " +
            "ORDER BY r.player_score DESC " +
            "LIMIT 2")
    Optional<List<Player>> findWinnerAndSecondOfGroup(@Param("tournamentGroupId") Long tournamentGroupId, @Param("tournamentId") Long tournamentId);
}
