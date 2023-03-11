package de.hsos.control.admin;

import de.hsos.boundry.admin.IAdminManagement;
import de.hsos.entity.catalogue.IAdminCatalogue;
import de.hsos.shared.dto.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
@RequestScoped
public class AdminManager implements IAdminManagement {

    @Inject
    IAdminCatalogue adminCatalogue;

    @Override
    public Collection<TrainerWithLinkDTO> getAllUsers() {
        return this.adminCatalogue.getAllUser();
    }

    @Override
    public Optional<TrainerFullDTO> getUserById(Long id) {
        return this.adminCatalogue.getUserById(id);
    }


    @Override
    public Optional<PokemonExistsDTO> addPokemonToUser(Long id, List<PokemonLightDTO> pokemonLightDTOList) {
        return this.adminCatalogue.addPokemon(id, pokemonLightDTOList);
    }

    @Override
    public Optional<PokemonExistsDTO> deletePokemonFromUser(Long userId, List<PokemonLightDTO> pokemonLightDTOList) {
        return this.adminCatalogue.deletePokemon(userId, pokemonLightDTOList);
    }
}
