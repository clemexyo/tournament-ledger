package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.*;
import com.example.dream_games_demo.model.Country;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.repository.CountryRepository;
import com.example.dream_games_demo.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {
    @Mock
    private PlayerRepository mockPlayerRepository;
    @Mock
    private CountryService mockCountryService;

    @InjectMocks
    private PlayerService underTest;

    @Test
    void allPlayers_ThereExistsAPlayer() {
        //given
        Player testPlayer = new Player("test");

        List<Player> playerList = Collections.singletonList(testPlayer);

        //mocking allPlayers method to return the above country list.
        when(mockPlayerRepository.findAll()).thenReturn(playerList);

        //when
        List<String> expected = underTest.allPlayers();

        //then
        assertAll(
                () -> assertTrue(expected.contains(testPlayer.toString())),
                () -> assertTrue(expected.size() == 1),
                () -> assertFalse(expected.isEmpty())
        );
    }
    @Test
    void allPlayers_NoPlayerFound() {
        //given

        //when

        //then
        assertThrows(NoPlayerFoundException.class,
                () -> underTest.allPlayers());
    }

    @Test
    void findPlayerById_Success() {
        //given
        Country country = new Country("UK");
        Player testPlayer = new Player(country, 1L, 1L);

        //mocking the mock repo to return the above player instance when findByIs is called.
        when(mockPlayerRepository.findById(anyLong())).thenReturn(Optional.of(testPlayer));

        //when
        Player expected = underTest.findPlayerById(1l);

        //then
        assertEquals(expected, testPlayer);
        verify(mockPlayerRepository, times(1)).findById(anyLong());
    }
    @Test
    void findPlayerById_Fail() {
        //given

        //when

        //then
        assertThrows(PlayerNotFoundException.class,
                () -> underTest.findPlayerById(anyLong()));
    }

    @Test
    void createPlayer_Success() {
        //given
        String playerName = "testName";
        List<Country> countryList = Collections.singletonList(new Country("USA"));
        when(mockCountryService.allCountries()).thenReturn(countryList);

        //when
        String expected = underTest.createPlayer(playerName);

        //then
        verify(mockPlayerRepository, times(1)).save(any(Player.class));
        assertNotNull(expected);
        assertTrue(expected.contains(playerName));
    }
    @Test
    void createPlayer_Fail() {
        //given
        String playerName = "testName";

        //when

        //then
        assertThrows(CreatePlayerException.class,
                () -> underTest.createPlayer(playerName));
    }
    @Test
    void updateLevel_Success() {
    }
    @Test
    void updateLevel_Fail() {
    }
    @Test
    void playerStatus() {
    }

    @Test
    void claimReward() {
    }
}