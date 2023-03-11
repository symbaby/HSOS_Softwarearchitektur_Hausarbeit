package de.hsos.control.user;

import de.hsos.boundry.user.IUserManagement;
import de.hsos.entity.catalogue.IUserCatalogue;
import de.hsos.shared.dto.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
@RequestScoped
public class UserManager implements IUserManagement {

    @Inject
    IUserCatalogue userCatalogue;


    @Override
    public Optional<PrincipalDTO> getUserData(String trainerName) {
        return this.userCatalogue.getUserData(trainerName);
    }

    @Override
    public Collection<ObtainedPokemonWithLinkDTO> getAllPokemon(String trainerName) {

        return this.userCatalogue.getAllPokemon(trainerName);
    }

    @Override
    public Optional<PokemonExistsDTO> getPokemonById(Long pokemonId, String trainerName) {
        return this.userCatalogue.getPokemonById(pokemonId, trainerName);
    }

    @Override
    public Collection<PokemonWithLinkDTO> getFavoritePokemon(String trainerName) {

        return this.userCatalogue.getFavoritePokemon(trainerName);
    }

    @Override
    public Optional<PokemonWithLinkDTO> setFavoritePokemon(Long pokemonId, String trainerName) {
        return this.userCatalogue.setFavoritePokemon(pokemonId, trainerName);
    }

    @Override
    public Optional<PokemonWithLinkDTO> removeFavoritePokemon(Long pokemonId, String trainerName) {
        return this.userCatalogue.removeFavoritePokemon(pokemonId, trainerName);
    }

    @Override
    public Optional<BoosterPackDTO> buyBooster(String trainerName) {
        return this.userCatalogue.buyBooster(trainerName);
    }

    @Override
    public Optional<BoosterPackDTO> obtainFreeBooster(String trainerName) {
        return this.userCatalogue.obtainFreeBooster(trainerName);
    }

    @Override
    public Optional<TrainerFullDTO> registerNewUser(String trainerName) {
        return this.userCatalogue.registerNewUser(trainerName);
    }


}
