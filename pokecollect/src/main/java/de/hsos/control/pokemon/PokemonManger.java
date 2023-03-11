package de.hsos.control.pokemon;

import de.hsos.boundry.pokemon.IPokemonManagement;
import de.hsos.entity.catalogue.IPokemonCatalogue;
import de.hsos.shared.dto.PokemonFullDTO;
import de.hsos.shared.dto.PokemonWithLinkDTO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
@RequestScoped
public class PokemonManger implements IPokemonManagement {

    @Inject
    IPokemonCatalogue pokemonCatalogue;

    @Override
    public Optional<PokemonFullDTO> getPokemonById(Long pokemonId) {
        return this.pokemonCatalogue.getPokemonById(pokemonId);
    }

    @Override
    public Collection<PokemonWithLinkDTO> getAllPokemon() {
        return this.pokemonCatalogue.getAllPokemon();
    }

    @Override
    public Collection<PokemonWithLinkDTO> getAllPokemonWithPaging(Integer pageIndex) {
        return this.pokemonCatalogue.getAllPokemonWithPaging(pageIndex);
    }

    @Override
    public void initDB() {
        this.pokemonCatalogue.initDB();
    }
}
