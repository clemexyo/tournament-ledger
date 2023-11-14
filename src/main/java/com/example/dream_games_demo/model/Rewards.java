package com.example.dream_games_demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rewards")
public class Rewards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "bigint default 0")
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "tournament_group_id")
    private TournamentGroup tournament_group;

    @Column(columnDefinition = "boolean default false")
    private Boolean is_claimed;
}
