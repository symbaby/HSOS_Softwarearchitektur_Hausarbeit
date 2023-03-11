package de.hsos.shared.dto;

import de.hsos.shared.utils.ConfigSetting;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: beryildi, jbelasch
 */

public class PokemonLightDTO {

    @NotNull
    @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON)
    public Long pokemonId;

    @NotNull
    @Min(1)
    public int amount;

    public PokemonLightDTO(){}

    public PokemonLightDTO(Long pokemonId, int amount) {
        this.pokemonId = pokemonId;
        this.amount = amount;
    }

}
