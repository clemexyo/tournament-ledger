package com.example.dream_games_demo.controller;

import com.example.dream_games_demo.model.Player;
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
    public ResponseEntity<String> createPlayer(@RequestBody Map<String, Object> requestBody){
        String user_name = (String) requestBody.get("user_name");
        String createdPlayer = playerService.createPlayer(user_name);
        return new ResponseEntity<String>(createdPlayer, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateLevel(@RequestBody Map<String, Object> requestBody){
        Long id = ((Number) requestBody.get("id")).longValue();
        return new ResponseEntity<String>(playerService.updateLevel(id), HttpStatus.OK);
    }
}
