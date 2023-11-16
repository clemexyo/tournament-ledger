package com.example.dream_games_demo.model;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournament_groups")
public class TournamentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;

    @ManyToOne
    @JoinColumn(name = "player3_id")
    private Player player3;

    @ManyToOne
    @JoinColumn(name = "player4_id")
    private Player player4;

    @ManyToOne
    @JoinColumn(name = "player5_id")
    private Player player5;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player winner;

    @Column(columnDefinition = "bigint default 0")
    private Long total_group_score;

    @Column(columnDefinition = "Boolean default false")
    private Boolean is_active;

    public TournamentGroup(){

    }
    public TournamentGroup(Player player, Tournament latest_tournament){
        if(player1 == null){
            this.player1 = player;
        }
        else if(player2 == null){
            this.player2 = player;
        }
        else if(player3 == null){
            this.player3 = player;
        }
        else if(player4 == null){
            this.player4 = player;
        }
        else if(player5 == null){
            this.player5 = player;
        }
        else {
            //we will hope for the best and the controllers added before this piece of code will be
            //enough that, so we will never end up in this exception.
            throw new IllegalStateException("No empty column available to assign the player.");
        }
        this.is_active = false;
        this.total_group_score = 0L;
        this.tournament = latest_tournament;
    }
    public void addPlayer(Player player){
        if(player1 == null){
            this.player1 = player;
        }
        else if(player2 == null){
            this.player2 = player;
        }
        else if(player3 == null){
            this.player3 = player;
        }
        else if(player4 == null){
            this.player4 = player;
        }
        else if(player5 == null){
            this.player5 = player;
        }
        else {
            //we will hope for the best and the controllers added before this piece of code will be
            //enough that, so we will never end up in this exception.
            throw new IllegalStateException("No empty column available to assign the player.");
        }
    }
    public boolean isFull(){
        return this.player1 != null &&
                this.player2 != null &&
                this.player3 != null &&
                this.player4 != null &&
                this.player5 != null;
    }

    // Getters and setters
    public List<String> tournamentPlayerCountries(){
        List<String> countries = new ArrayList<String>();
        countries.add(this.player1 != null ? this.player1.getCountry() : "");
        countries.add(this.player2 != null ? this.player2.getCountry() : "");
        countries.add(this.player3 != null ? this.player3.getCountry() : "");
        countries.add(this.player4 != null ? this.player4.getCountry() : "");
        countries.add(this.player5 != null ? this.player5.getCountry() : "");
        return countries;
    }
    public void setIsActive(boolean updated_is_active){
        this.is_active = updated_is_active;
    }
    public boolean getIsActive() { return this.is_active; }
    public void setWinner(Player winner) { this.winner = winner; }
    public Long getId() { return this.id; }
    public Long getTotal_group_score() { return this.total_group_score; }
    public void setTotal_group_score(Long group_score) { this.total_group_score = group_score; }
}
