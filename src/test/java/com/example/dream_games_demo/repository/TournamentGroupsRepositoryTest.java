package com.example.dream_games_demo.repository;

import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TournamentGroupsRepositoryTest {
    @Autowired
    TournamentGroupsRepository underTest;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;
    private Tournament tournament;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("test");
        playerRepository.save(player);

        tournament = new Tournament();
        tournamentRepository.save(tournament);
    }

    @Test
    @DisplayName(value = "This test should return all of the groups of a tournament which have not started yet. Which means they are not active and there is at least one empty spot.")
    void findPendingTournamentGroups() {
        //given
        TournamentGroup tournamentGroup = new TournamentGroup(player, tournament);
        underTest.save(tournamentGroup);

        //when
        Optional<List<TournamentGroup>> expectedGroups = underTest.findPendingTournamentGroups(tournament.getId());

        //then
        assertTrue(expectedGroups.isPresent());

        List<TournamentGroup> expectedGroupList = expectedGroups.get();

        assertFalse(expectedGroupList.isEmpty());

        TournamentGroup expectedGroup = expectedGroupList.get(0);

        assertAll(
                () -> assertFalse(expectedGroup.isFull()),
                () -> assertFalse(expectedGroup.getIsActive()),
                () -> assertTrue(expectedGroup.getWinner() == null),
                () -> assertTrue(expectedGroup.getSecond() == null),
                () -> assertEquals(expectedGroup.getTournament(), tournament)
        );
    }

    @Test
    @DisplayName(value = "This test should if the given player is in an active group or not.")
    void findActiveTournamentGroupByPlayerId() {
        //given
        TournamentGroup tournamentGroup = new TournamentGroup(player, tournament);

        Player player2 = new Player();
        playerRepository.save(player2);

        Player player3 = new Player();
        playerRepository.save(player3);

        Player player4 = new Player();
        playerRepository.save(player4);

        Player player5 = new Player();
        playerRepository.save(player5);

        tournamentGroup.addPlayer(player2);
        tournamentGroup.addPlayer(player3);
        tournamentGroup.addPlayer(player4);
        tournamentGroup.addPlayer(player5);

        tournamentGroup.setIsActive(true);
        underTest.save(tournamentGroup);

        //when
        Optional<TournamentGroup> optionalTournamentGroup = underTest.findActiveTournamentGroupByPlayerId(player.getId());

        //then
        assertTrue(optionalTournamentGroup.isPresent());

        TournamentGroup expectedGroup = optionalTournamentGroup.get();

        assertAll(
                () -> assertTrue(expectedGroup.getIsActive()),
                () -> assertTrue(
                        player == expectedGroup.getPlayer1() ||
                        player == expectedGroup.getPlayer2() ||
                        player == expectedGroup.getPlayer3() ||
                        player == expectedGroup.getPlayer4() ||
                        player == expectedGroup.getPlayer5()
                )
        );
    }

    @Test
    @DisplayName(value = "This test should all tournament groups of the provided tournament.")
    void findAllGroupsByTournament() {
        //given
        TournamentGroup tournamentGroup = new TournamentGroup(player, tournament);
        underTest.save(tournamentGroup);

        //when
        Optional<List<TournamentGroup>> optionalTournamentGroups = underTest.findAllGroupsByTournament(tournament.getId());

        //then
        assertTrue(optionalTournamentGroups.isPresent());

        List<TournamentGroup> expectedGroupList = optionalTournamentGroups.get();

        assertFalse(expectedGroupList.isEmpty());

        assertAll(
                () -> assertFalse(expectedGroupList.isEmpty()),
                () -> assertEquals(1, expectedGroupList.size()),
                () -> assertEquals(tournament, expectedGroupList.get(0).getTournament())
        );
    }

    @Test
    @DisplayName(value = "This test should find the last group provided player attended that is not active. This will be used to claim reward.")
    void findLastGroupOfPlayer() {
        //given
        TournamentGroup tournamentGroup = new TournamentGroup(player, tournament);
        underTest.save(tournamentGroup);

        //when
        Optional<TournamentGroup> optionalTournamentGroup = underTest.findLastGroupOfPlayer(player.getId());

        //then
        assertTrue(optionalTournamentGroup.isPresent());

        TournamentGroup expectedGroup = optionalTournamentGroup.get();

        assertAll(
                () -> assertFalse(expectedGroup.getIsActive()),
                () -> assertTrue(
                        player == expectedGroup.getPlayer1() ||
                                player == expectedGroup.getPlayer2() ||
                                player == expectedGroup.getPlayer3() ||
                                player == expectedGroup.getPlayer4() ||
                                player == expectedGroup.getPlayer5()
                )
        );
    }
}