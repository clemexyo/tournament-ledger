package com.example.dream_games_demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rewards")
public class Rewards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "bigint default 0")
    private Long player_score;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "tournament_group_id")
    private TournamentGroup tournament_group;

    public Rewards() {
        this.player = null;
        this.player_score = 0L;
        this.tournament = null;
        this.tournament_group = null;
    }
    public Rewards(Player player, Tournament tournament, TournamentGroup tournament_group) {
        this.player = player;
        this.tournament = tournament;
        this.tournament_group = tournament_group;
        this.player_score = 0L;
    }
    public Player getPlayer() { return this.player; }
    public Long getScore() { return this.player_score; }
}
