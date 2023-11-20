package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.NoTournamentGroupFoundException;
import com.example.dream_games_demo.exceptions.PlayerCannotEnterTournamentException;
import com.example.dream_games_demo.exceptions.PlayerNotFoundException;
import com.example.dream_games_demo.exceptions.RewardNotFoundException;
import com.example.dream_games_demo.model.*;
import com.example.dream_games_demo.repository.CountryRepository;
import com.example.dream_games_demo.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyLong;


@DataJpaTest
class PlayerServiceTest {
    @Autowired
    private PlayerService underTest;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CountryService countryService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private TournamentGroupService tournamentGroupService;
    @Autowired
    private ScoresService scoresService;
    @Autowired
    TournamentService tournamentService;

    private final List<String> countryNames = Arrays.asList("c1", "c2", "c3", "c4", "c5");
    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;
    private Player p5;

    @BeforeEach
    void setUp(){
        Country c1 = new Country(1L, "c1");
        Country c2 = new Country(2L, "c2");
        Country c3 = new Country(3L, "c3");
        Country c4 = new Country(4L, "c4");
        Country c5 = new Country(5L, "c5");
        countryRepository.saveAndFlush(c1);
        countryRepository.saveAndFlush(c2);
        countryRepository.saveAndFlush(c3);
        countryRepository.saveAndFlush(c4);
        countryRepository.saveAndFlush(c5);

        p1 = new Player(c1, 20L, 1L);
        p2 = new Player(c2, 20L, 2L);
        p3 = new Player(c3, 20L, 3L);
        p4 = new Player(c4, 20L, 4L);
        p5 = new Player(c5, 20L, 5L);
        playerRepository.saveAndFlush(p1);
        playerRepository.saveAndFlush(p2);
        playerRepository.saveAndFlush(p3);
        playerRepository.saveAndFlush(p4);
        playerRepository.saveAndFlush(p5);
    }
    @Test
    @Transactional
    @Disabled
    void allPlayers() {
        //given

        //when
        List<String> expected = underTest.allPlayers();

        //then
        assertFalse(expected.isEmpty());

    }
    @Test
    @Transactional
    @Disabled
    void findPlayerById_Success() {
        //given

        //when
        Player expected = underTest.findPlayerById(1L);

        //then
        assertAll(
                () -> assertNotNull(expected)
        );

    }
    @Test
    @Transactional
    @Disabled
    void findPlayerById_Fail() {
        //given

        //when

        //then
        assertThrows(PlayerNotFoundException.class,
                () -> underTest.findPlayerById(anyLong()));
    }

    @Test
    @Transactional
    @Disabled
    void createPlayer_Success() {
        //given
        String testName = "test";

        //when
        String expected = underTest.createPlayer(testName);

        //then
        assertNotNull(expected);

    }
    @Test
    @Transactional
    @Disabled
    void updateLevel_PlayerInActiveGroup() {
        //given
        Tournament tournament = tournamentService.createTournament();

        tournamentService.enterTournament(1L, tournament);
        tournamentService.enterTournament(2L, tournament);
        tournamentService.enterTournament(3L, tournament);
        tournamentService.enterTournament(4L, tournament);
        tournamentService.enterTournament(5L, tournament);

        //when
        String expected = underTest.updateLevel(1L);
        Scores score = scoresService.findByPlayerAndTournamentGroup(1L, 1L);

        //then
        assertAll(
                () -> assertTrue(score.getScore() == 1), //player gained a score
                () -> assertTrue(expected.split(",")[2].contains("21")) //player gained a level
        );

    }
    @Test
    @Transactional
    @Disabled
    void updateLevel_PlayerNotInActiveGroup(){
        //given
        Tournament tournament = tournamentService.createTournament();

        tournamentService.enterTournament(1L, tournament);
        tournamentService.enterTournament(2L, tournament);

        //when
        String expected = underTest.updateLevel(1L);
        Scores score = scoresService.findByPlayerAndTournamentGroup(1L, 1L);

        //then
        assertAll(
                () -> assertTrue(score.getScore() == 0), //player did not gain any score
                () -> assertTrue(expected.split(",")[2].contains("21")) //player gained a level
        );


    }
    @Test
    @Transactional
    @Disabled
    void updateLevel_Fail() {
        //given

        //when

        //then
        assertThrows(PlayerNotFoundException.class,
                () -> underTest.updateLevel(15L));
    }
    @Test
    @Transactional
    @Disabled
    void playerStatus_Success() {
        //given
        Tournament tournament = tournamentService.createTournament();

        //when
        TournamentGroup tournamentGroup = tournamentService.enterTournament(1L, tournament);

        //then
        assertAll(
                () -> assertFalse(tournamentGroup.getPlayer1().getCan_enter()),
                () -> assertTrue(tournamentGroup.getPlayer1().getCoins() == 4000)
        );

    }
    @Test
    @Transactional
    @Disabled
    void playerStatus_Fail() {
        //given
        p1.setLevel(5L); //player cannot enter tournament with this level.
        playerRepository.saveAndFlush(p1);

        //when

        //then
        assertThrows(PlayerCannotEnterTournamentException.class,
                () -> underTest.playerStatus(1L));
        assertAll(
                () -> assertTrue(p1.getCoins() == 5000),
                () -> assertTrue(p1.getCan_enter())
        );
    }

    @Test
    @Transactional
    @Disabled
    void claimReward_Winner() {
        //given
        Tournament tournament = tournamentService.createTournament();

        tournamentService.enterTournament(1L, tournament);
        TournamentGroup tournamentGroup = tournamentService.enterTournament(2L, tournament);
        tournamentGroup.setWinner(p1);

        //when
        underTest.claimReward(p1);

        //then
        assertAll(
                () -> assertTrue(p1.getCan_enter()),
                () -> assertEquals(15000, p1.getCoins())
        );
    }
    @Test
    @Transactional
    @Disabled
    void claimReward_Second() {
        //given
        Tournament tournament = tournamentService.createTournament();

        tournamentService.enterTournament(1L, tournament);
        TournamentGroup tournamentGroup = tournamentService.enterTournament(2L, tournament);
        tournamentGroup.setSecond(p1);

        //when
        underTest.claimReward(p1);

        //then
        assertAll(
                () -> assertTrue(p1.getCan_enter()),
                () -> assertEquals(10000, p1.getCoins())
        );
    }
    @Test
    @Transactional
    @Disabled
    void claimReward_Fail() {
        //given
        Tournament tournament = tournamentService.createTournament();

        tournamentService.enterTournament(1L, tournament);
        TournamentGroup tournamentGroup = tournamentService.enterTournament(2L, tournament);
        tournamentService.enterTournament(3L, tournament);
        tournamentGroup.setWinner(p1);
        tournamentGroup.setSecond(p2);

        //when


        //then
        assertThrows(RewardNotFoundException.class,
                () -> underTest.claimReward(p3));
    }
    @Test
    @Transactional
    @Disabled
    void claimReward_NoTournamentGroupFound() {
        //given
        Tournament tournament = tournamentService.createTournament();

        tournamentService.enterTournament(1L, tournament);
        TournamentGroup tournamentGroup = tournamentService.enterTournament(2L, tournament);

        tournamentGroup.setWinner(p1);
        tournamentGroup.setSecond(p2);

        //when


        //then
        assertThrows(NoTournamentGroupFoundException.class,
                () -> underTest.claimReward(p3));
    }
}