package com.example.dream_games_demo.service;

import com.example.dream_games_demo.exceptions.TournamentNotActiveException;
import com.example.dream_games_demo.exceptions.TournamentNotFoundException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import com.example.dream_games_demo.model.TournamentGroup;
import com.example.dream_games_demo.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TournamentGroupService tournamentGroupService;

    public Tournament createTournament(){
        Tournament tournament = new Tournament();
        tournamentRepository.save(tournament);
        return tournament;
    }
    public Tournament tournamentStatus(){
        Tournament tournament = findLatestTournament(); //throws TournamentNotFoundException is there's no tournament.
        if(tournament.getisActive()){
            return tournament;
        }
        throw new TournamentNotActiveException();
    }
    public TournamentGroup enterTournament(Long playerId, Tournament latest_tournament){
        Optional<List<TournamentGroup>> optionalPendingTournamentGroups = tournamentGroupService.findPendingTournamentGroups(latest_tournament.getId());
        TournamentGroup to_return = null;
        //at this point we know the player that sent the request is valid
        Player player = playerService.findPlayerById(playerId);
        if(!optionalPendingTournamentGroups.get().isEmpty()){
            List<TournamentGroup> pendingTournamentGroups = optionalPendingTournamentGroups.get();

            //here we know that there is at least one available tournament group.
            to_return = tournamentGroupService.assignPlayerToAvailableGroup(player, pendingTournamentGroups, latest_tournament);
        }
        else{
            //Since there are either no available groups or no groups at all,
            //we will create a new TournamentGroup instance and assign the player there in which
            //the player can wait for other players to join the group and start the game.
            to_return = tournamentGroupService.createGroupAndAssignPlayer(player, latest_tournament);
        }
        return to_return;
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void startTournament(){
        Tournament tournament = new Tournament();
        tournamentRepository.save(tournament);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void endTournament(){
        Tournament latestTournament = findLatestTournament();
        if(!latestTournament.getisActive()){
            throw new TournamentNotActiveException();
        }
        latestTournament.endTournament();
        tournamentRepository.save(latestTournament);
        tournamentGroupService.endTournamentGroups(latestTournament.getId());
    }
    private Tournament findLatestTournament() {
        Optional<Tournament> optionalTournament = tournamentRepository.findTopByOrderByIdDesc();
        if(optionalTournament.isEmpty()) {
            throw new TournamentNotFoundException();
        }
        return optionalTournament.get();
    }
}
