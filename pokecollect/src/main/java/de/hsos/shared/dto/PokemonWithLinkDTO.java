package de.hsos.shared.dto;

import de.hsos.shared.utils.ConfigSetting;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * @Author: beryildi, jbelasch
 */
public class PokemonWithLinkDTO {
    // Der mit Link
    @NotNull
    public URI url;

    @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON)
    public Long pokemonId;


    public PokemonWithLinkDTO(Long pokemonId){
        this.pokemonId = pokemonId;
    }

    public PokemonWithLinkDTO(){}
}
