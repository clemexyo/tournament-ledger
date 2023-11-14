package com.example.dream_games_demo.model;
import com.example.dream_games_demo.model.Player;
import com.example.dream_games_demo.model.Tournament;
import jakarta.persistence.*;

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
    public TournamentGroup(Player player){
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
    }
    public String currentBoard(){
        return "to be implemented";
    }
    public Player getPlayer1() { return this.player1; }
    public Player getPlayer2() { return this.player2; }
    public Player getPlayer3() { return this.player3; }
    public Player getPlayer4() { return this.player4; }
    public Player getPlayer5() { return this.player5; }
    public void setPlayer1(Player player) { this.player1 = player; }
    public void setPlayer2(Player player) { this.player2 = player; }
    public void setPlayer3(Player player) { this.player3 = player; }
    public void setPlayer4(Player player) { this.player4 = player; }
    public void setPlayer5(Player player) { this.player5 = player; }

    // Getters and setters
}
