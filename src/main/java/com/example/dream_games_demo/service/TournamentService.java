package com.example.dream_games_demo.service;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TournamentGroupService tournamentGroupService;

    public Map<String, Object> tournamentStatus(){
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;

        Optional<Tournament> optionalTournament = tournamentRepository.findTopByOrderByIdDesc();

        if(optionalTournament.isPresent()){
            Tournament tournament = optionalTournament.get();
            if(tournament.getisActive()){
                message = "Valid Tournament";
                httpStatus = HttpStatus.OK;
            }
            else{
                message = "Tournament is not active";
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }
        else{
            message = "Tournament not found";
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("httpStatus", httpStatus);
        return map;
    }
    public Map<String, Object> enterTournament(Long playerId){
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;

        Optional<List<TournamentGroup>> optionalPendingTournamentGroups = tournamentGroupService.findPendingTournamentGroups();

        //at this point we know the player that sent the request is valid
        Player player = playerService.findPlayerById(playerId).get();
        if(!optionalPendingTournamentGroups.get().isEmpty()){
            List<TournamentGroup> pendingTournamentGroups = optionalPendingTournamentGroups.get();

            //here we know that there is at least one available tournament group.
            Map<String, Object> currentStatus = tournamentGroupService.assignPlayerToAvailableGroup(player, pendingTournamentGroups);
        }
        else{
            //Since there are either no available groups or no groups at all,
            //we will create a new TournamentGroup instance and assign the player there in which
            //the player can wait for other players to join the group and start the game.
            Map<String, Object> currentStatus = tournamentGroupService.createGroupAndAssignPlayer(player);
            message = (String) currentStatus.get("message");
            httpStatus = (HttpStatus) currentStatus.get("httpStatus");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("httpStatus", httpStatus);
        return map;
    }
    public Tournament findLatestTournament(){
        return tournamentRepository.findTopByOrderByIdDesc().get();
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
