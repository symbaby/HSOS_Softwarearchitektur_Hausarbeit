package de.hsos.shared.dto;

import de.hsos.shared.utils.ConfigSetting;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: beryildi, jbelasch
 */
public class PokemonTradeDTO {
    @NotNull
    @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON)
    public Long offeredPokemonId; // Das was wir anbieten

    @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON)
    public Long acceptedPokemonId; // Das was wir wollen

    public PokemonTradeDTO(){}

    public PokemonTradeDTO(Long offeredPokemonId, Long acceptedPokemonId) {
        this.offeredPokemonId = offeredPokemonId;
        this.acceptedPokemonId = acceptedPokemonId;
    }
}
