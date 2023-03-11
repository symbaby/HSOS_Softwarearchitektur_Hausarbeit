package de.hsos.shared.dto;


/**
 * @Author: beryildi, jbelasch
 */
public class PokemonExistsDTO {
    // Der mit Allem
    public Long id;

    public int amount;

    public boolean isFavorite;


    public PokemonExistsDTO(Long id, int amount, boolean isFavorite) {
        this.id = id;
        this.amount = amount;
        this.isFavorite = isFavorite;
    }
}
