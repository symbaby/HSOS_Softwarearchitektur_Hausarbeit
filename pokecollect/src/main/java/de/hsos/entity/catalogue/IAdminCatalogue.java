package de.hsos.entity.catalogue;

import de.hsos.shared.dto.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
public interface IAdminCatalogue {
    Collection<TrainerWithLinkDTO> getAllUser();
    Optional<TrainerFullDTO> getUserById(Long id);
    //Collection<PokemonWithLinkDTO> getAllPokemonFromUser(Long userId);
    Optional<PokemonExistsDTO> addPokemon(Long userId, List<PokemonLightDTO> pokemonLightDTOList);
    Optional<PokemonExistsDTO> deletePokemon(Long userId, List<PokemonLightDTO> pokemonLightDTOList);
}
