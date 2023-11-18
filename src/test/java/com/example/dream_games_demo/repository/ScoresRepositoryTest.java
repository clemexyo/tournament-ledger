package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Scores;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ScoresRepositoryTest {

    @Autowired
    private ScoresRepository underTest;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TournamentGroupsRepository tournamentGroupsRepository;

    private Player player;
    private Tournament tournament;
    private TournamentGroup tournamentGroup;

    @BeforeEach
    void setUp(){
        tournament = new Tournament();
        tournamentRepository.save(tournament);

        player = new Player();
        playerRepository.save(player);

        tournamentGroup = new TournamentGroup(player, tournament);
        tournamentGroupsRepository.save(tournamentGroup);
    }

    @Test
    @DisplayName(value = "This test should return all scores of the tournament group, provided the tournament_group_id")
    void getAllScoresOfTournamentGroup() {
        //given
        Scores score = new Scores(player, tournament, tournamentGroup);
        underTest.save(score);

        //when
        Optional<List<Scores>> expected = underTest.getAllScoresOfTournamentGroup(tournamentGroup.getId());

        //then
        assertTrue(expected.isPresent());

        List<Scores> scoresList = expected.get();

        assertAll(
                () -> assertFalse(scoresList.isEmpty()),
                () -> assertEquals(1, scoresList.size()),
                () -> assertEquals(score, scoresList.get(0)),
                () -> assertEquals(score.getTournamentGroup(), tournamentGroup),
                () -> assertEquals(score.getTournament(), tournament)
        );
    }

    @Test
    @DisplayName(value = "This test should return the score instance associated with a player and tournament group, provided the player_id and tournament_group_id")
    void findByPlayerAndTournamentGroup() {
        //given
        Scores score = new Scores(player, tournament, tournamentGroup);
        underTest.save(score);

        //when
        Optional<Scores> expected = underTest.findByPlayerAndTournamentGroup(player.getId(), tournamentGroup.getId());

        //then
        assertTrue(expected.isPresent());

        Scores expectedScore = expected.get();

        assertAll(
                () -> assertEquals(expectedScore.getPlayer(), player),
                () -> assertEquals(expectedScore.getTournament(), tournament),
                () -> assertEquals(expectedScore.getTournamentGroup(), tournamentGroup)
        );
    }

    @Test
    @DisplayName(value = "This test should return all scores of the given tournament. Provided tournament_id")
    void findByTournament() {
        //given
        Scores score = new Scores(player, tournament, tournamentGroup);
        underTest.save(score);


        //when
        Optional<List<Scores>> expected = underTest.findByTournament(tournament.getId());

        //then
        assertTrue(expected.isPresent());

        List<Scores> expectedScoresList = expected.get();

        assertAll(
                () -> assertFalse(expectedScoresList.isEmpty()),
                () -> assertEquals(1, expectedScoresList.size()),
                () -> assertEquals(score, expectedScoresList.get(0)),
                () -> assertEquals(score.getTournament(), tournament)
        );
    }
}