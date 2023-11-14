package com.example.dream_games_demo.model;
import com.example.dream_games_demo.model.Country;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "string default X")
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

    @Column(columnDefinition = "bigint default 0")
    private Long group_score;

    public Player(String user_name) {
        this.user_name = user_name;
        this.coins = 5000L;
        this.level = 1L;
        this.can_enter = true;
    }
    public Player() {
        this.user_name = "X";
        this.coins = 5000L;
        this.level = 1L;
        this.can_enter = false;
    }
    public void setUserName(String user_name) { this.user_name = user_name; }
    public String getUserName() { return this.user_name; }
    public Long getId(){ return this.id; }
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
        return "Player {user_name: " + user_name + ", id: " + id + ", leve: " + level + ", coins: " + coins + ", country: " + country + ", can_enter: " + can_enter + "}";
    }
}

