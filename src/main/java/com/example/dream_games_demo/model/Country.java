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

    public Country(){

    }
    public Country(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return "Country {id: " + id + ", name: " + name + "}";
    }

    // Getters and setters
    public String getName() { return this.name == null ? "" : this.name; }
}
