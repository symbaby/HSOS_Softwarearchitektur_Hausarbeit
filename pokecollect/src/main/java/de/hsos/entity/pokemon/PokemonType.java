package de.hsos.entity.pokemon;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Objects;

/**
 * @Author: beryildi, jbelasch
 */

@Entity
public class PokemonType extends PanacheEntityBase {


    @Id
    @SequenceGenerator(
            name = "PokemonTypeSequence",
            sequenceName = "PokemonType_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String primaryType;

    private String secondaryType;


    public PokemonType() {
    }

    public PokemonType(String primaryType, String secondaryType) {
        this.primaryType = Objects.requireNonNull(primaryType);
        this.secondaryType = Objects.requireNonNull(secondaryType);
    }

    public PokemonType(String primaryType) {
        this.primaryType = Objects.requireNonNull(primaryType);
    }


    public PokemonType(Types primaryType) {
        this.primaryType = Objects.requireNonNull(primaryType).name();
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primary) {
        this.primaryType = primary;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(String secondary) {
        this.secondaryType = secondary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}

