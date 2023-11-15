package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentGroupsRepository extends JpaRepository<TournamentGroup, Long> {

    @Query("SELECT tg FROM TournamentGroup tg " +
            "WHERE tg.is_active = false AND " +
            "(tg.player1 IS NULL OR tg.player2 IS NULL OR tg.player3 IS NULL OR tg.player4 IS NULL OR tg.player5 IS NULL) " +
            "ORDER BY tg.id ASC")
    Optional<List<TournamentGroup>> findPendingTournamentGroups();
}
