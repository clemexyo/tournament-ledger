package com.example.dream_games_demo.service;

import com.example.dream_games_demo.repository.RewardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewardsService {
    @Autowired
    private RewardsRepository rewardsRepository;

    
}
