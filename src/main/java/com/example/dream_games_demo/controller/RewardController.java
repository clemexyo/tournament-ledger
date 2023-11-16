package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.RewardNotFoundException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.requests.ClaimRewardRequest;
import com.example.dream_games_demo.service.PlayerService;
import com.example.dream_games_demo.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reward")
public class RewardController {
    @Autowired
    private RewardsService rewardsService;
    @Autowired
    private PlayerService playerService;
    @PutMapping
    public ResponseEntity<String> claimReward(@RequestBody ClaimRewardRequest request){
        if(request == null || request.getPlayerId() == null || !(request.getPlayerId() instanceof  Long)){
            //throw new InvalidClaimRewardRequestException();
        }
        Long player_id = request.getPlayerId();
        Player player = playerService.findPlayerById(player_id); //if there's no such player, this will throw an exception.
        if(!player.getCan_enter()){
            throw new RewardNotFoundException();
        }
        playerService.claimReward(player);
        return new ResponseEntity<>(player.toString(), HttpStatus.OK);
    }
}
