package com.example.dream_games_demo.model;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_name;

    @Column(columnDefinition = "bigint default 1")
    private Long level;

    @Column(columnDefinition = "bigint default 5000")
    private Long coins;

    @Column(columnDefinition = "boolean default false")
    private Boolean can_enter;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public Player() {
        this.user_name = "X";
        this.coins = 5000L;
        this.level = 1L;
        this.can_enter = false;
    }
    public Player(String user_name) {
        this.user_name = user_name;
        this.coins = 5000L;
        this.level = 1L;
        this.can_enter = true;
    }
    public Player(Country country, Long level, Long id) { //this constructor is created to make the testing process less painful.
        this.country = country;
        this.coins = 5000L;
        this.level = level;
        this.can_enter = true;
        this.user_name = "test";
        this.id = id;
    }

    public String getUserName() { return this.user_name; }
    public Long getId(){ return this.id; }
    public void setId(Long id) { this.id = id; }
    public void setCountry(Country country){
        this.country = country;
    }
    public String getCountry() { return this.country == null ? "" : this.country.getName(); }
    public void setLevel(Long level){
        this.level = level;
    }
    public Long getLevel(){
        return this.level;
    }
    public void setCoins(Long coins){
        this.coins = coins;
    }
    public Long getCoins(){
        return this.coins;
    }
    public Boolean getCan_enter(){ return this.can_enter; }
    public void setCan_enter(Boolean updatedCan_enter) { this.can_enter = updatedCan_enter; }
    @Override
    public String toString(){
        return "{user_name: " + user_name + ", id: " + id + ", level: " + level + ", coins: " + coins + ", country: " + country + ", can_enter: " + can_enter + "}";
    }
    public void payToEnter() { this.coins -= 1000; }
    public void getPaymentBack() { this.coins += 1000; }
    public void getWinnerPrize() { this.coins += 10000; }
    public void getSecondPrize() { this.coins += 5000; }
}

