package de.hsos.shared.dto;

import java.util.List;

/**
 * @Author: beryildi, jbelasch
 */
public class BoosterPackDTO {

    public List<Long> pokemonIdList;

    public BoosterPackDTO(List<Long> pokemonIdList) {
        this.pokemonIdList = pokemonIdList;
    }
}
