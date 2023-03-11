package de.hsos.boundry.admin;

import de.hsos.shared.dto.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
public interface IAdminManagement {
    Collection<TrainerWithLinkDTO> getAllUsers();
    Optional<TrainerFullDTO> getUserById(Long id);
    //Collection<PokemonWithLinkDTO>getAllPokemon(Long userId);
    Optional<PokemonExistsDTO> addPokemonToUser(Long id, List<PokemonLightDTO> pokemonLightDTOList);
    Optional<PokemonExistsDTO> deletePokemonFromUser(Long userId, List<PokemonLightDTO> pokemonLightDTOList);
}
