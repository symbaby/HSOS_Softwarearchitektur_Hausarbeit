package de.hsos.boundry.user;

import de.hsos.shared.dto.*;

import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
public interface IUserManagement {

    Optional<PrincipalDTO> getUserData(String trainerName);
    Collection<ObtainedPokemonWithLinkDTO> getAllPokemon(String trainerName);
    Optional<PokemonExistsDTO> getPokemonById(Long pokemonId, String trainerName);
    Collection<PokemonWithLinkDTO> getFavoritePokemon(String trainerName);
    Optional<PokemonWithLinkDTO> setFavoritePokemon(Long pokemonId, String trainerName);
    Optional<PokemonWithLinkDTO> removeFavoritePokemon(Long pokemonId, String trainerName);
    Optional<BoosterPackDTO> buyBooster(String trainerName);
    Optional<BoosterPackDTO> obtainFreeBooster(String trainerName);

    Optional<TrainerFullDTO> registerNewUser(String trainerName);

}
