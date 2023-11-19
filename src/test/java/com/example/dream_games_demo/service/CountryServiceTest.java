package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.CountryNotFoundException;
import com.example.dream_games_demo.exceptions.CreateCountryException;
import com.example.dream_games_demo.exceptions.NoCountryFoundException;
import com.example.dream_games_demo.model.*;
import com.example.dream_games_demo.repository.*;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @Mock
    private ScoresRepository mockScoresRepository;
    @Mock
    private CountryRepository mockCountryRepository;
    @Mock
    private PlayerRepository mockPlayerRepository;
    @Mock
    private TournamentRepository mockTournamentRepository;
    @Mock
    private TournamentGroupsRepository mockTournamentGroupsRepository;
    @Mock
    private ScoresService mockScoresService;
    @InjectMocks
    private CountryService underTest;

    @Test
    void createCountry_Success() {
        //given
        String testCountryName = "TestCountryName";

        //when
        String result = underTest.createCountry(testCountryName);

        //then
        verify(mockCountryRepository, times(1)).save(any(Country.class));
        assertNotNull(result);
        assertTrue(result.contains(testCountryName));
    }
    @Test
    void createCountry_Fail(){
        //given
        String testCountryName = "TestCountryName";

        //when
        //Mocking the repository to throw a CreateCountryException when save is called
        when(mockCountryRepository.save(any(Country.class))).thenThrow(CreateCountryException.class);

        //then
        assertThrows(CreateCountryException.class,
                () -> underTest.createCountry(testCountryName));
    }

    @Test
    void getAllCountries_ThereExistsACountry() {
        //given
        Country testCountry = new Country("testCountryName");
        List<Country> countryList = Collections.singletonList(testCountry);

        //mocking allCountries method to return the above country list.
        when(mockCountryRepository.findAll()).thenReturn(countryList);

        //when
        List<Country> expected = underTest.allCountries();

        //then
        assertAll(
                () -> assertTrue(expected.contains(testCountry)),
                () -> assertTrue(expected.size() == 1),
                () -> assertFalse(expected.isEmpty())
        );
    }
    @Test
    void getAllCountries_TheresNoCountryToGet(){
        //given
        //nothing is going to be given for the purpose of this test.

        //when
        //there's no reason to perform any task. Because allCounties method of the CountryService is designed to throw an exception when the list is empty.

        //then
        assertThrows(NoCountryFoundException.class,
                () -> underTest.allCountries());
    }

    @Test
    void findCountryById_Success() {
        //given
        Country testCountry = new Country("testCountry");

        //mocking the mock repo to return the above country instance when findByIs is called.
        when(mockCountryRepository.findById(anyLong())).thenReturn(Optional.of(testCountry));

        //when
        Country expected = underTest.findCountryById(1l);

        //then
         assertEquals(expected, testCountry);
         verify(mockCountryRepository, times(1)).findById(anyLong());
    }
    @Test
    void findCountryById_Fail() {
        //given
        //we are not going to provide any country instance.

        //when
        //there shouldn't be any expected value here. findCountryById is designed to throw a CountryNotFoundException in case of not finding a country.

        //then
        assertThrows(CountryNotFoundException.class,
                () -> underTest.findCountryById(anyLong()));
    }

    @Nested
    class LeaderBoardTests {
        //variables to generate two full tournament groups, will be used to test leaderboards.
        //players of tournament group 1
        private Player p1_1;
        private Player p2_1;
        private Player p3_1;
        private Player p4_1;
        private Player p5_1;

        //players of tournament groups 2
        private Player p1_2;
        private Player p2_2;
        private Player p3_2;
        private Player p4_2;
        private Player p5_2;

        //tournament groups
        private TournamentGroup tg_1;
        private TournamentGroup tg_2;

        //tournament itself
        private Tournament tournament;

        //countries
        private Country c_1;
        private Country c_2;
        private Country c_3;
        private Country c_4;
        private Country c_5;

        //player scores;
        Scores s1_1;
        Scores s2_1;
        Scores s3_1;
        Scores s4_1;
        Scores s5_1;
        Scores s1_2;
        Scores s2_2;
        Scores s3_2;
        Scores s4_2;
        Scores s5_2;

        @BeforeEach
        void setUpTournamentGroups(){
            //create countries
            c_1 = new Country(1L,"Turkey"); mockCountryRepository.saveAndFlush(c_1);
            c_2 = new Country(2L,"UK"); mockCountryRepository.saveAndFlush(c_2);
            c_3 = new Country(3L,"USA"); mockCountryRepository.saveAndFlush(c_3);
            c_4 = new Country(4L,"France"); mockCountryRepository.saveAndFlush(c_4);
            c_5 = new Country(5L,"Germany"); mockCountryRepository.saveAndFlush(c_5);

            //create players and assign them to countries, note that each country will be assigned to two players.
            //also, players will start from level 20 since they have to be at least 20 to enter a tournament.
            //please see player service tests for entering tournament test.
            p1_1 = new Player(c_1, 20L, 1L); mockPlayerRepository.saveAndFlush(p1_1);
            p2_1 = new Player(c_2, 20L, 2L); mockPlayerRepository.saveAndFlush(p2_1);
            p3_1 = new Player(c_3, 20L, 3L); mockPlayerRepository.saveAndFlush(p3_1);
            p4_1 = new Player(c_4, 20L, 4L); mockPlayerRepository.saveAndFlush(p4_1);
            p5_1 = new Player(c_5, 20L, 5L); mockPlayerRepository.saveAndFlush(p5_1);

            p1_2 = new Player(c_1, 20L, 6L); mockPlayerRepository.saveAndFlush(p1_2);
            p2_2 = new Player(c_2, 20L, 7L); mockPlayerRepository.saveAndFlush(p2_2);
            p3_2 = new Player(c_3, 20L, 8L); mockPlayerRepository.saveAndFlush(p3_2);
            p4_2 = new Player(c_4, 20L, 9L); mockPlayerRepository.saveAndFlush(p4_2);
            p5_2 = new Player(c_5, 20L, 10L); mockPlayerRepository.saveAndFlush(p5_2);

            //create tournament
            tournament = new Tournament(); mockTournamentRepository.saveAndFlush(tournament);

            //create tournament groups
            tg_1 = new TournamentGroup(tournament, p1_1, p2_1, p3_1, p4_1, p5_1, 1L); mockTournamentGroupsRepository.saveAndFlush(tg_1);
            tg_2 = new TournamentGroup(tournament, p1_2, p2_2, p3_2, p4_2, p5_2, 2L); mockTournamentGroupsRepository.saveAndFlush(tg_2);

            //and finally, we must have appropriate scores.
            s1_1 = new Scores(p1_1, tournament, tg_1); mockScoresRepository.saveAndFlush(s1_1);
            s2_1 = new Scores(p2_1, tournament, tg_1); mockScoresRepository.saveAndFlush(s2_1);
            s3_1 = new Scores(p3_1, tournament, tg_1); mockScoresRepository.saveAndFlush(s3_1);
            s4_1 = new Scores(p4_1, tournament, tg_1); mockScoresRepository.saveAndFlush(s4_1);
            s5_1 = new Scores(p5_1, tournament, tg_1); mockScoresRepository.saveAndFlush(s5_1);

            s1_2 = new Scores(p1_2, tournament, tg_2); mockScoresRepository.saveAndFlush(s1_2);
            s2_2 = new Scores(p2_2, tournament, tg_2); mockScoresRepository.saveAndFlush(s2_2);
            s3_2 = new Scores(p3_2, tournament, tg_2); mockScoresRepository.saveAndFlush(s3_2);
            s4_2 = new Scores(p4_2, tournament, tg_2); mockScoresRepository.saveAndFlush(s4_2);
            s5_2 = new Scores(p5_2, tournament, tg_2); mockScoresRepository.saveAndFlush(s5_2);
        }

        @Test
        void generateCountryLeaderBoard_NoEqualScores() {
            //given
            //in below scenario, Turkey's total score is 30. Its leading player is player1_2 with score 20, second is player1_1 with score 10.
            s1_1.setScore(10L); s1_1.setLatestUpdateToNow();
            s1_2.setScore(20L); s1_2.setLatestUpdateToNow();

            //telling country repo to return Turkey when called.
            when(mockCountryRepository.findById(anyLong())).thenReturn(Optional.of(c_1));

            //telling scores service to return below list when getScoresByTournament is called.
            List<Scores> scoresList = new ArrayList<>(Arrays.asList(s1_1, s2_1, s3_1, s4_1, s5_1, s1_2, s2_2, s3_2, s4_2, s5_2));
            when(mockScoresService.getScoresByTournament(tournament.getId())).thenReturn(scoresList);

            //when
            String expectedLeaderboard = underTest.generateCountryLeaderBoard(tournament.getId(), c_1.getId());
            String[] expectedLeaderboardLines = expectedLeaderboard.split("\n");

            assertAll(
                    () -> assertTrue(expectedLeaderboardLines[2].contains("20")), //player with score 20 is the leader.
                    () -> assertTrue(expectedLeaderboardLines[3].contains("10")) //player with score 10 is the second.
            );
        }
        @Test
        void generateCountryLeaderBoard_WithEqualScores() {
            //given
            //in below scenario, Turkey's total score is 20. Both players have the same score however player1_2 is the most recent player to achieve a point.
            // Therefore, it will be the leader
            s1_1.setScore(10L); s1_1.setLatestUpdateToNow();
            try { //a slight delay is necessary to ensure different timestamps.
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            s1_2.setScore(10L); s1_2.setLatestUpdateToNow();

            //telling country repo to return Turkey when called.
            when(mockCountryRepository.findById(anyLong())).thenReturn(Optional.of(c_1));

            //telling scores service to return below list when getScoresByTournament is called.
            List<Scores> scoresList = new ArrayList<>(Arrays.asList(s1_1, s2_1, s3_1, s4_1, s5_1, s1_2, s2_2, s3_2, s4_2, s5_2));
            when(mockScoresService.getScoresByTournament(tournament.getId())).thenReturn(scoresList);

            //when
            String expectedLeaderboard = underTest.generateCountryLeaderBoard(tournament.getId(), c_1.getId());
            String[] expectedLeaderboardLines = expectedLeaderboard.split("\n");

            assertAll(
                    () -> assertTrue(expectedLeaderboardLines[2].contains("6")), //player with id = 6 is the leader.
                    () -> assertTrue(expectedLeaderboardLines[3].contains("1")) //player with id = 1 is the second.
            );
        }

        @Test
        void generateCountriesLeaderBoard_NoEqualScores() {
            //given
            //in below scenario, Turkey's total score is 30 and UK's total score is 15.
            s1_1.setScore(10L); s1_1.setLatestUpdateToNow();
            s2_1.setScore(15L); s2_1.setLatestUpdateToNow();
            s1_2.setScore(20L); s1_2.setLatestUpdateToNow();

            //telling scores service to return below list when getScoresByTournament is called.
            List<Scores> scoresList = new ArrayList<>(Arrays.asList(s1_1, s2_1, s3_1, s4_1, s5_1, s1_2, s2_2, s3_2, s4_2, s5_2));
            when(mockScoresService.getScoresByTournament(tournament.getId())).thenReturn(scoresList);

            //telling country repo to return below countries.
            List<Country> countryList = new ArrayList<>(Arrays.asList(c_1, c_2, c_3, c_4, c_5));
            when(mockCountryRepository.findAll()).thenReturn(countryList);

            //when
            String expectedLeaderboard = underTest.generateCountriesLeaderBoard(tournament.getId());
            String[] leaderboardLines = expectedLeaderboard.split("\n");

            //then
            assertAll(
                    () -> assertTrue(leaderboardLines[2].contains("Turkey")), //the winner is Turkey. Since the first 2 lines are information about the board we are skipping them.
                    () -> assertTrue(leaderboardLines[3].contains("UK"))    //second is UK,
            );
        }
        @Test
        void generateCountriesLeaderBoard_WithEqualScore() {
            //given
            //in below scenario, Turkey's total score is 15 and UK's total score is 15. However since the UK is the last one to score a point, it should be higher in the list.
            s1_1.setScore(10L); s1_1.setLatestUpdateToNow();
            //keep in mind we need to introduce some delay between setLatestUpdateToNow() method calls in order to ensure a different timestamp for each.
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            s1_2.setScore(5L); s1_2.setLatestUpdateToNow();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            s2_1.setScore(15L); s2_1.setLatestUpdateToNow();

            //telling scores service to return below list when getScoresByTournament is called.
            List<Scores> scoresList = new ArrayList<>(Arrays.asList(s1_1, s2_1, s3_1, s4_1, s5_1, s1_2, s2_2, s3_2, s4_2, s5_2));
            when(mockScoresService.getScoresByTournament(tournament.getId())).thenReturn(scoresList);

            //telling country repo to return below countries.
            List<Country> countryList = new ArrayList<>(Arrays.asList(c_1, c_2, c_3, c_4, c_5));
            when(mockCountryRepository.findAll()).thenReturn(countryList);

            //when
            String expectedLeaderboard = underTest.generateCountriesLeaderBoard(tournament.getId());
            String[] leaderboardLines = expectedLeaderboard.split("\n");

            System.out.println(expectedLeaderboard);
            //then
            assertAll(
                    () -> assertTrue(leaderboardLines[2].contains("UK")),  //the winner is UK. Since the first 2 lines are information about the board we are skipping them.
                    () -> assertTrue(leaderboardLines[3].contains("Turkey"))  //second is Turkey,
            );
        }
    }
}