package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TournamentRepositoryTest {
    @Autowired
    private TournamentRepository underTest;

    @Test
    void findTopByOrderByIdDesc() {
        //given
        Tournament tournament_1 = new Tournament();
        Tournament tournament_2 = new Tournament();

        underTest.save(tournament_1);
        underTest.save(tournament_2);

        //when
        Optional<Tournament> optionalTournament = underTest.findTopByOrderByIdDesc();

        //then
        assertTrue(optionalTournament.isPresent());

        Tournament expected = optionalTournament.get();

        assertAll(
                () -> assertTrue(expected.getisActive()),
                () -> assertEquals(tournament_2.getId(), expected.getId())
        );
    }
}