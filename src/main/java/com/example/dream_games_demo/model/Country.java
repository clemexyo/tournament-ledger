package com.example.dream_games_demo.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "country")
    private List<Player> player;

    @Override
    public String toString(){
        return "Country {id: " + id + ", name: " + name + "}";
    }

    public String getName() { return this.name == null ? "" : this.name; }
    // Getters and setters
}
