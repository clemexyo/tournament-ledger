package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Rewards;
import com.example.dream_games_demo.model.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, Long> {
    @Query("SELECT r FROM Rewards r WHERE r.tournament_group.id = :tournamentGroupId")
    List<Rewards> findAllByTournamentGroup(@Param("tournamentGroupId") Long tournamentGroupId);
}
