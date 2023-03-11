package de.hsos.acl.pokemonDTOfromAPI;

/**
 * @Author: beryildi, jbelasch
 */
public class PokemonDTOFromAPI {

    // POKEMON API STRUCTURE
    public String name;
    public Long id;
    public Sprites sprites;
    public PokemonAPITypes[] types;


    public PokemonDTOFromAPI(String name, Long id, Sprites sprites, PokemonAPITypes[] types) {
        this.name = name;
        this.id = id;
        this.sprites = sprites;
        this.types = types;
    }

    public PokemonDTOFromAPI() {

    }

}
