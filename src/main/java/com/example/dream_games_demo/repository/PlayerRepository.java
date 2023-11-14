package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT CONCAT(' {user_name=', p.user_name, ' id=', p.id, ', level=', p.level, ', coins=', p.coins, ', can_enter=', p.can_enter, ', country=', p.country, ' }') FROM Player p")
    List<String> findAllPlayers();

    Optional<Player> findById(Long id);
}
