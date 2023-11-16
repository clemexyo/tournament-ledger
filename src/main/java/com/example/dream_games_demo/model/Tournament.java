package com.example.dream_games_demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime start_time;
    private LocalDateTime end_time;

    @Column(columnDefinition = "boolean default false")
    private boolean is_active;

    public Tournament(){
        this.start_time = LocalDateTime.now();
        this.is_active = true;
    }

    public Boolean getisActive(){
        return this.is_active;
    }
    public Long getId() { return this.id; }

    public void endTournament(){
        this.is_active = false;
        this.end_time = LocalDateTime.now();
    }

    // Getters and setters
}
