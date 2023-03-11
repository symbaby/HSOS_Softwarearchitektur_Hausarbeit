package de.hsos.entity.catalogue;

import de.hsos.shared.dto.PokemonFullDTO;
import de.hsos.shared.dto.PokemonWithLinkDTO;

import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
public interface IPokemonCatalogue {

    Optional<PokemonFullDTO> getPokemonById(Long pokemonId);

    Collection<PokemonWithLinkDTO> getAllPokemon();

    Collection<PokemonWithLinkDTO> getAllPokemonWithPaging(Integer pageIndex);

    void initDB();
}
