package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.exceptions.InvalidCreatePlayerRequestException;
import com.example.dream_games_demo.exceptions.InvalidUpdatePlayerLevelRequestException;
import com.example.dream_games_demo.exceptions.NoPlayerFoundException;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.requests.CreatePlayerRequest;
import com.example.dream_games_demo.requests.UpdatePlayerLevelRequest;
import com.example.dream_games_demo.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<String>> getAllPlayers(){
        return new ResponseEntity<List<String>>(playerService.allPlayers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createPlayer(@RequestBody CreatePlayerRequest request){
        if(request == null || request.getUserName() == null || request.getUserName() == ""){
            throw new InvalidCreatePlayerRequestException();
        }
        String user_name = request.getUserName();
        String createdPlayer = playerService.createPlayer(user_name);
        return new ResponseEntity<String>(createdPlayer, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateLevel(@RequestBody UpdatePlayerLevelRequest request){
        if(request == null || request.getPlayerId() == null || !(request.getPlayerId() instanceof Long)){
            throw new InvalidUpdatePlayerLevelRequestException();
        }
        Long id = request.getPlayerId();
        return new ResponseEntity<String>(playerService.updateLevel(id), HttpStatus.OK);
    }
}
