package de.hsos.gateway.admin;

import de.hsos.entity.catalogue.IAdminCatalogue;
import de.hsos.entity.pokemon.ObtainedPokemon;
import de.hsos.entity.user.Trainer;
import de.hsos.shared.converter.DTOConverter;
import de.hsos.shared.dto.PokemonExistsDTO;
import de.hsos.shared.dto.PokemonLightDTO;
import de.hsos.shared.dto.TrainerFullDTO;
import de.hsos.shared.dto.TrainerWithLinkDTO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: beryildi, jbelasch
 */

@RequestScoped
public class AdminRepository implements IAdminCatalogue {

    @Inject
    DTOConverter converter;

    @Override
    public Collection<TrainerWithLinkDTO> getAllUser() {
        List<Trainer> trainerList = Trainer.listAll();

        return trainerList.stream().map(converter::toUserWithLinkDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<TrainerFullDTO> getUserById(Long id) {

        Optional<Trainer> oTrainer = Optional.ofNullable(Trainer.findById(id));

        if (oTrainer.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(converter.toTrainerFullDTO(oTrainer.get()));
        }
    }


    /**
     * iterate through pokemonLightDTOList and check if oTrainer.get().getObtainedPokemon() contains the id of pokemonLightDTO,
     * if yes correct the specific obtainedPokemon amount with the amount of the specific pokemonLightDTO,
     * if no add the pokemonLightDTO to the obtained Pokèmon
     *
     * @param userId              identifier of the user
     * @param pokemonLightDTOList transfer object
     * @return Optional.of(PokemonFullDTO) that contains all necessary information about the Pokèmon
     */
    @Override
    public Optional<PokemonExistsDTO> addPokemon(Long userId, List<PokemonLightDTO> pokemonLightDTOList) {

        Optional<Trainer> oTrainer = Optional.ofNullable(Trainer.findById(userId));

        if (oTrainer.isEmpty()) {
            return Optional.empty();
        }

        for (PokemonLightDTO pokemonLightDTO : pokemonLightDTOList) {
            Optional<ObtainedPokemon> op = oTrainer.get().getObtainedPokemon().stream()
                    .filter(p -> p.getPokemonId().equals(pokemonLightDTO.pokemonId))
                    .findFirst();

            if (op.isPresent()) {
                op.get().setAmount(op.get().getAmount() + pokemonLightDTO.amount);
                return Optional.ofNullable(converter.toPokemonExistDTO(op.get()));

            } else {

                ObtainedPokemon obtainedPokemon = new ObtainedPokemon(oTrainer.get(), pokemonLightDTO.pokemonId, pokemonLightDTO.amount);
                oTrainer.get().getObtainedPokemon().add(obtainedPokemon);
                return Optional.ofNullable(converter.toPokemonExistDTO(obtainedPokemon));
            }
        }

        return Optional.empty();
    }


    /**
     * iterate through pokemonLightDTOList and check if oTrainer.get().getObtainedPokemon() contains the id of pokemonLightDTO,
     * if yes decrease the amount of the specific obtained Pokèmon amount with the amount of the specific pokemonLightDTO,
     * if the amount is lower than 1 delete the obtained Pokèmon from the obtainedPokemon list
     *
     * @param userId              identifier of the user
     * @param pokemonLightDTOList transfer object
     * @return Optional.of(PokemonFullDTO) that contains all necessary information about the Pokèmon
     */
    @Override
    public Optional<PokemonExistsDTO> deletePokemon(Long userId, List<PokemonLightDTO> pokemonLightDTOList) {

        Optional<Trainer> oTrainer = Optional.ofNullable(Trainer.findById(userId));

        if (oTrainer.isEmpty()) {
            return Optional.empty();
        }

        List<ObtainedPokemon> obtainedPokemonList = oTrainer.get().getObtainedPokemon();
        for (PokemonLightDTO pokemonLightDTO : pokemonLightDTOList) {
            Optional<ObtainedPokemon> foundPokemon = obtainedPokemonList.stream()
                    .filter(op -> op.getPokemonId().equals(pokemonLightDTO.pokemonId))
                    .findFirst();
            if (foundPokemon.isPresent()) {
                ObtainedPokemon obtainedPokemon = foundPokemon.get();
                if (obtainedPokemon.getAmount() - pokemonLightDTO.amount < 1) {
                    obtainedPokemonList.remove(obtainedPokemon);
                } else {
                    obtainedPokemon.setAmount(obtainedPokemon.getAmount() - pokemonLightDTO.amount);
                }
                return Optional.ofNullable(converter.toPokemonExistDTO(obtainedPokemon));
            } else {
                throw new IllegalArgumentException("Pokemon with pokemon id : " + pokemonLightDTO.pokemonId + " does not exist");
            }
        }

        return Optional.empty();
    }
}
