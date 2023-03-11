package de.hsos.gateway.pokemon;

import de.hsos.acl.gateway.PokemonGateway;
import de.hsos.acl.pokemonDTOfromAPI.PokemonDTOFromAPI;
import de.hsos.entity.pokemon.Pokemon;
import de.hsos.entity.pokemon.PokemonType;
import de.hsos.entity.catalogue.IPokemonCatalogue;
import de.hsos.shared.converter.DTOConverter;
import de.hsos.shared.dto.PokemonFullDTO;
import de.hsos.shared.dto.PokemonWithLinkDTO;
import de.hsos.shared.utils.ConfigSetting;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author: beryildi, jbelasch
 */

@RequestScoped
public class PokemonRepository implements PanacheRepository<Pokemon> , IPokemonCatalogue {


    @Inject
    @RestClient
    PokemonGateway pokemonGateway;

    @Inject
    DTOConverter converter;


    public void initPokemonDB(@Observes StartupEvent event) {
        this.initDB();
    }

    public void initDBOld() {

        // 151 wenn alles laeuft
        for (int i = 1; i <= 10; i++) {
            PokemonDTOFromAPI pokemonDTOFromAPI = this.pokemonGateway.getPokemonById((long) i);

            Pokemon pokemon;
            if (pokemonDTOFromAPI.types.length == 2) {
                pokemon = new Pokemon(pokemonDTOFromAPI.name,
                        new PokemonType(pokemonDTOFromAPI.types[0].type.name, pokemonDTOFromAPI.types[1].type.name),
                        pokemonDTOFromAPI.sprites.other.dream_world.front_default);
            } else {
                pokemon = new Pokemon(pokemonDTOFromAPI.name,
                        new PokemonType(pokemonDTOFromAPI.types[0].type.name),
                        pokemonDTOFromAPI.sprites.other.dream_world.front_default);
            }
            persist(pokemon);
        }
    }

    @Override
    @Transactional
    public void initDB() {
        IntStream.range(ConfigSetting.MIN_POKEMON, ConfigSetting.MAX_POKEMON)
                .mapToObj(i -> this.pokemonGateway.getPokemonById((long) i))
                .map(pokemonDTOFromAPI -> {
                    PokemonType type;
                    if (pokemonDTOFromAPI.types.length == 2) {
                        type = new PokemonType(pokemonDTOFromAPI.types[0].type.name, pokemonDTOFromAPI.types[1].type.name);
                    } else {
                        type = new PokemonType(pokemonDTOFromAPI.types[0].type.name);
                    }
                    return new Pokemon(pokemonDTOFromAPI.name, type, pokemonDTOFromAPI.sprites.other.dream_world.front_default);
                }).forEach(this::persist);
    }


    @Override
    public Optional<PokemonFullDTO> getPokemonById(Long pokemonId) {

        Optional<Pokemon> oPokemon = Optional.ofNullable(findById(pokemonId));
        if (oPokemon.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(this.converter.toPokemonFullDTO(findById(pokemonId)));
        }
    }

    @Override
    public Collection<PokemonWithLinkDTO> getAllPokemon(){
        Collection<Pokemon> pokemonCollection = listAll();

        return pokemonCollection.stream().map(converter::toPokemonWithLinkDTO).collect(Collectors.toList());
    }

    @Override
    public Collection<PokemonWithLinkDTO> getAllPokemonWithPaging(Integer pageIndex) {
        PanacheQuery<Pokemon> pokemonCollection = Pokemon.findAll();

        return pokemonCollection.page(Page.of(pageIndex, 30)) // Size = Pokemon per Page
                .list()
                .stream()
                .map(converter::toPokemonWithLinkDTO)
                .collect(Collectors.toList());

    }
}