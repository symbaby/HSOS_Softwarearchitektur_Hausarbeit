package de.hsos.shared.dto;

import java.net.URI;

/**
 * @Author: beryildi, jbelasch
 */
public class ObtainedPokemonWithLinkDTO {
    // Der mit Link
    public URI url;
    public Long pokemonId;
    public int amount;


    public ObtainedPokemonWithLinkDTO(Long pokemonId, int amount) {
        this.pokemonId = pokemonId;
        this.amount = amount;
    }

    public ObtainedPokemonWithLinkDTO() {
    }
}