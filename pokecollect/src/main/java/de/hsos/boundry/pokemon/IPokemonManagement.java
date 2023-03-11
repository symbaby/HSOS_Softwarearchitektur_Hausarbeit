package de.hsos.boundry.pokemon;

import de.hsos.shared.dto.ObtainedPokemonWithLinkDTO;
import de.hsos.shared.dto.PokemonFullDTO;
import de.hsos.shared.dto.PokemonWithLinkDTO;

import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
public interface IPokemonManagement {

    Optional<PokemonFullDTO> getPokemonById(Long pokemonId);

    Collection<PokemonWithLinkDTO> getAllPokemon();

    Collection<PokemonWithLinkDTO> getAllPokemonWithPaging(Integer pageIndex);
    void initDB();
}
