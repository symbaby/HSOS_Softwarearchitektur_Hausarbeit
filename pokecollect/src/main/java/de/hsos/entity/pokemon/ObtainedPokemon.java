package de.hsos.entity.pokemon;

import de.hsos.entity.user.Trainer;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @Author: beryildi, jbelasch
 */
@Entity
public class ObtainedPokemon extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "ObtainedPokemonSequence",
            sequenceName = "ObtainedPokemon_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ObtainedPokemonSequence")
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
    private Long pokemonId;
    private int amount;
    private boolean isFavorite;

    public ObtainedPokemon() {

    }

    public ObtainedPokemon(Trainer trainer, Long pokemonId, int amount) {
        this.trainer = Objects.requireNonNull(trainer);
        this.pokemonId = Objects.requireNonNull(pokemonId);
        this.amount = amount;
        this.isFavorite = false;
    }

    public ObtainedPokemon(Trainer trainer, Long pokemonId, int amount, boolean isFavorite) {
        this.trainer = Objects.requireNonNull(trainer);
        this.pokemonId = Objects.requireNonNull(pokemonId);
        this.amount = amount;
        this.isFavorite = isFavorite;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(Long pokemonId) {
        this.pokemonId = pokemonId;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }

    public void decreaseAmount(int amount){
        this.amount -= amount;
    }


}
