package de.hsos.entity.user;

import de.hsos.entity.pokemon.ObtainedPokemon;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author: beryildi, jbelasch
 */
@Entity
public class Trainer extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "TrainerSequence",
            sequenceName = "Trainer_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TrainerSequence")
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    private CoinPurse coinPurse;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObtainedPokemon> obtainedPokemon;

    private LocalDateTime freeBoosterTimer;

    public Trainer() {
        this.obtainedPokemon = new ArrayList<>();
    }

    public Trainer(String username, CoinPurse coinPurse) {
        this.username = Objects.requireNonNull(username);
        this.coinPurse = Objects.requireNonNull(coinPurse);
        this.obtainedPokemon = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public CoinPurse getCoin() {
        return coinPurse;
    }

    public void setCoin(CoinPurse coinPurse) {
        this.coinPurse = coinPurse;
    }

    public CoinPurse getCoinPurse() {
        return coinPurse;
    }

    public void setCoinPurse(CoinPurse coinPurse) {
        this.coinPurse = coinPurse;
    }


    public List<ObtainedPokemon> getObtainedPokemon() {
        return obtainedPokemon;
    }

    public void setObtainedPokemon(ArrayList<ObtainedPokemon> obtainedPokemon) {
        this.obtainedPokemon = obtainedPokemon;
    }

    public LocalDateTime getFreeBoosterTimer() {
        return freeBoosterTimer;
    }

    public void setFreeBoosterTimer(LocalDateTime freeBoosterTimer) {
        this.freeBoosterTimer = freeBoosterTimer;
    }
}
