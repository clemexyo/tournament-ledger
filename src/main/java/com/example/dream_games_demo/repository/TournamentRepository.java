package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("SELECT t FROM Tournament t ORDER BY t.id DESC LIMIT 1")
    Optional<Tournament> findTopByOrderByIdDesc();

    Optional<Tournament> findTournamentById(Long id);
}
