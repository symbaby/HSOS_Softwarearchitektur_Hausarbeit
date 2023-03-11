package de.hsos.shared.dto;

import java.net.URI;

/**
 * @Author: beryildi, jbelasch
 */
public class ObtainedPokemonDTO {
    public Long pokemonId;
    public int amount;
    public URI url;

    public ObtainedPokemonDTO() {

    }

    public ObtainedPokemonDTO(Long pokemonId, int amount) {
        this.pokemonId = pokemonId;
        this.amount = amount;
    }


}
