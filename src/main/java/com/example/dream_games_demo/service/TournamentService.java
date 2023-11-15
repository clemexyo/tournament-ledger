package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.TournamentNotActiveException;
import com.example.dream_games_demo.exceptions.TournamentNotFoundException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.PlayerRepository;
import com.example.dream_games_demo.repository.TournamentGroupsRepository;
import com.example.dream_games_demo.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TournamentGroupService tournamentGroupService;

    public List<Object> tournamentStatus(){
        boolean valid_tournament = false;
        Optional<Tournament> optionalTournament = tournamentRepository.findTopByOrderByIdDesc();
        if(optionalTournament.isPresent()){
            Tournament tournament = optionalTournament.get();
            if(tournament.getisActive()){
                valid_tournament = true;
            }
            else{
                throw new TournamentNotActiveException();
            }
        }
        else{
            throw new TournamentNotFoundException();
        }
        return Arrays.asList(valid_tournament, optionalTournament.get());
    }
    public String enterTournament(Long playerId, Tournament latest_tournament){
        Optional<List<TournamentGroup>> optionalPendingTournamentGroups = tournamentGroupService.findPendingTournamentGroups();
        String leaderBoard = "";

        //at this point we know the player that sent the request is valid
        Player player = playerService.findPlayerById(playerId).get();
        if(!optionalPendingTournamentGroups.get().isEmpty()){
            List<TournamentGroup> pendingTournamentGroups = optionalPendingTournamentGroups.get();

            //here we know that there is at least one available tournament group.
            leaderBoard = tournamentGroupService.assignPlayerToAvailableGroup(player, pendingTournamentGroups, latest_tournament);
        }
        else{
            //Since there are either no available groups or no groups at all,
            //we will create a new TournamentGroup instance and assign the player there in which
            //the player can wait for other players to join the group and start the game.
            leaderBoard = tournamentGroupService.createGroupAndAssignPlayer(player, latest_tournament);
        }
        return leaderBoard;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void startTournament(){
        Tournament tournament = new Tournament();
        tournamentRepository.save(tournament);
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void endTournament(){
        Optional<Tournament> latestTournament = tournamentRepository.findTopByOrderByIdDesc();
        if(latestTournament.isPresent()){
            latestTournament.get().endTournament();
            tournamentRepository.save(latestTournament.get());
        }

    }
}
