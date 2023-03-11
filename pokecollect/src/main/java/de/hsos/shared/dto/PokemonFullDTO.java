package de.hsos.shared.dto;

/**
 * @Author: beryildi, jbelasch
 */
public class PokemonFullDTO {

    public Long id;
    public String imageUri;
    public String name;
    public PokemonTypeDTO pokemonType;

    public PokemonFullDTO(){}

    public PokemonFullDTO(Long id, String imageUri, String name, PokemonTypeDTO pokemonType) {
        this.id = id;
        this.imageUri = imageUri;
        this.name = name;
        this.pokemonType = pokemonType;
    }
}
