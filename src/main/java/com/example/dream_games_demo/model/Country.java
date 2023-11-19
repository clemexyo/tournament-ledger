package com.example.dream_games_demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String name;

    public Country(){

    }
    public Country(String name) {
        this.name = name;
    }

    public Country(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString(){
        return "Country {id: " + id + ", name: " + name + "}";
    }

    // Getters and setters
    public String getName() { return this.name == null ? "" : this.name; }
    public Long getId() { return this.id; }
}
