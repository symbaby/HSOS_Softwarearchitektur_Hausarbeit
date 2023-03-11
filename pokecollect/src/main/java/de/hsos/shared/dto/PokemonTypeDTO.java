package de.hsos.shared.dto;

/**
 * @Author: beryildi, jbelasch
 */
public class PokemonTypeDTO {

    public String primaryType;
    public String secondaryType;

    public PokemonTypeDTO(){}

    public PokemonTypeDTO(String primaryType, String secondaryType) {
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
    }
}
