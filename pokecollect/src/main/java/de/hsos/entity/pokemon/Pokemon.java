package de.hsos.entity.pokemon;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Objects;

/**
 * @Author: beryildi, jbelasch
 */
@Entity
public class Pokemon extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "PokemonSequence",
            sequenceName = "Pokemon_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PokemonSequence")
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private PokemonType pokemonType;
    private String imageUri;

    public Pokemon() {
    }

    public Pokemon(String name, PokemonType pokemonType, String imageUri) {
        this.name = Objects.requireNonNull(name);
        this.pokemonType = Objects.requireNonNull(pokemonType);
        this.imageUri = Objects.requireNonNull(imageUri);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public PokemonType getPokemonType() {
        return pokemonType;
    }

    public void setPokemonType(PokemonType pokemonType) {
        this.pokemonType = pokemonType;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


}