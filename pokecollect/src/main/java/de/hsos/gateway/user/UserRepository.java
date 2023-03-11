package de.hsos.gateway.user;

import de.hsos.entity.user.BoosterPack;
import de.hsos.entity.user.CoinPurse;
import de.hsos.entity.pokemon.ObtainedPokemon;
import de.hsos.entity.user.Trainer;
import de.hsos.entity.catalogue.IUserCatalogue;
import de.hsos.shared.dto.*;
import de.hsos.shared.converter.DTOConverter;
import de.hsos.shared.utils.ConfigSetting;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: beryildi, jbelasch
 */

@RequestScoped
public class UserRepository implements IUserCatalogue, PanacheRepository<Trainer> {

    @Inject
    DTOConverter converter;

    @Transactional
    public void loadUsers(@Observes StartupEvent event) {
        Trainer t1 = new Trainer("berkan", new CoinPurse(100000));
        ObtainedPokemon obtainedPokemon = new ObtainedPokemon(t1, 1L, 1, true);
        ObtainedPokemon obtainedPokemon2 = new ObtainedPokemon(t1, 2L, 2, true);
        ObtainedPokemon obtainedPokemon3 = new ObtainedPokemon(t1, 3L, 3, false);
        ObtainedPokemon obtainedPokemon4 = new ObtainedPokemon(t1, 4L, 4, false);
        ObtainedPokemon obtainedPokemon5 = new ObtainedPokemon(t1, 5L, 5, false);
        ObtainedPokemon obtainedPokemon6 = new ObtainedPokemon(t1, 6L, 1, false);

        t1.getObtainedPokemon().add(obtainedPokemon);
        t1.getObtainedPokemon().add(obtainedPokemon2);
        t1.getObtainedPokemon().add(obtainedPokemon3);
        t1.getObtainedPokemon().add(obtainedPokemon4);
        t1.getObtainedPokemon().add(obtainedPokemon5);
        t1.getObtainedPokemon().add(obtainedPokemon6);
        Trainer.persist(t1);


        Trainer t2 = new Trainer("johannes", new CoinPurse(1000));
        ObtainedPokemon ot1 = new ObtainedPokemon(t2, 8L, 8, true);
        ObtainedPokemon ot2 = new ObtainedPokemon(t2, 9L, 9, true);
        ObtainedPokemon ot3 = new ObtainedPokemon(t2, 10L, 10, false);

        t2.getObtainedPokemon().add(ot1);
        t2.getObtainedPokemon().add(ot2);
        t2.getObtainedPokemon().add(ot3);
        Trainer.persist(t2);
    }


    @Override
    public Optional<PrincipalDTO> getUserData(String trainerName) {
        Optional<Trainer> oTrainer = getTrainer(trainerName);
        if (oTrainer.isPresent()) {
            Long trainerId = oTrainer.get().getId();
            int coinAmount = findById(trainerId).getCoinPurse().getBalance();

            return Optional.of(new PrincipalDTO(trainerName, coinAmount));
        }
        return Optional.empty();
    }

    @Override
    public Collection<ObtainedPokemonWithLinkDTO> getAllPokemon(String trainerName) {

        Optional<Trainer> oTrainer = getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            Long trainerId = oTrainer.get().getId();
            return findById(trainerId)
                    .getObtainedPokemon()
                    .stream()
                    .map(converter::toObtainedPokemonWithLinkDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Optional<PokemonExistsDTO> getPokemonById(Long pokemonId, String trainerName) {

        Optional<Trainer> oTrainer = getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            Long trainerId = oTrainer.get().getId();
            return findById(trainerId).getObtainedPokemon()
                    .stream()
                    .filter(op -> op.getPokemonId().equals(pokemonId))
                    .map(converter::toPokemonExistDTO)
                    .findFirst();
        }

        return Optional.empty();
    }

    @Override
    public Collection<PokemonWithLinkDTO> getFavoritePokemon(String trainerName) {

        Optional<Trainer> oTrainer = getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            Long trainerId = oTrainer.get().getId();
            return findById(trainerId).getObtainedPokemon()
                    .stream()
                    .filter(ObtainedPokemon::isFavorite)
                    .map(converter::toPokemonWithLinkDTO)
                    .collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public Optional<PokemonWithLinkDTO> setFavoritePokemon(Long pokemonId, String trainerName) {

        Optional<Trainer> oTrainer = getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            Long trainerId = oTrainer.get().getId();

            Optional<ObtainedPokemon> obtainedPokemon = this.getObtainedPokemon(pokemonId, trainerId);

            if (obtainedPokemon.isPresent()) {
                obtainedPokemon.get().setFavorite(true);
                return Optional.of(converter.toPokemonWithLinkDTO(obtainedPokemon.get()));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<PokemonWithLinkDTO> removeFavoritePokemon(Long pokemonId, String trainerName) {

        Optional<Trainer> oTrainer = getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            Long trainerId = oTrainer.get().getId();

            Optional<ObtainedPokemon> obtainedPokemon = this.getObtainedPokemon(pokemonId, trainerId);

            if (obtainedPokemon.isPresent()) {
                obtainedPokemon.get().setFavorite(false);
                return Optional.of(converter.toPokemonWithLinkDTO(obtainedPokemon.get()));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<BoosterPackDTO> buyBooster(String trainerName) {

        Optional<Trainer> oTrainer = getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            if (oTrainer.get().getCoinPurse().getBalance() >= 100) {
                oTrainer.get().getCoinPurse().setBalance(oTrainer.get().getCoinPurse().getBalance() - ConfigSetting.BOOSTER_PRICE);

                return this.addBoosterToCollection(oTrainer.get());
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<BoosterPackDTO> obtainFreeBooster(String trainerName) {

        Optional<Trainer> oTrainer = this.getTrainer(trainerName);

        if (oTrainer.isPresent()) {
            if (oTrainer.get().getFreeBoosterTimer() == null || (LocalDateTime.now().isAfter(oTrainer.get().getFreeBoosterTimer().plusMinutes(1)))) {

                Optional<BoosterPackDTO> boosterPackDTO = this.addBoosterToCollection(oTrainer.get());
                oTrainer.get().setFreeBoosterTimer(LocalDateTime.now());
                return boosterPackDTO;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TrainerFullDTO> registerNewUser(String trainerName) {

        Trainer trainer = new Trainer(trainerName, new CoinPurse(500));
        trainer.persist();

        return Optional.ofNullable(converter.toTrainerFullDTO(trainer));
    }


    private Optional<Trainer> getTrainer(String trainerName) {
        List<Trainer> trainerList = listAll();

        return trainerList.stream()
                .filter(trainer -> trainer.getUsername().equals(trainerName))
                .findFirst();
    }

    private Optional<ObtainedPokemon> getObtainedPokemon(Long pokemonId, Long trainerId) {

        return findById(trainerId).getObtainedPokemon()
                .stream()
                .filter(op -> op.getPokemonId().equals(pokemonId))
                .findFirst();
    }

    private Optional<BoosterPackDTO> addBoosterToCollection(Trainer trainer) {
        BoosterPack boosterPack = new BoosterPack();

        for (Long pokemonId : boosterPack.getPokemonIdList()) {
            Optional<ObtainedPokemon> foundPokemon = trainer.getObtainedPokemon().stream()
                    .filter(op -> op.getPokemonId().equals(pokemonId))
                    .findFirst();
            if (foundPokemon.isPresent()) {
                foundPokemon.get().setAmount(foundPokemon.get().getAmount() + 1);
            } else {
                ObtainedPokemon obtainedPokemon = new ObtainedPokemon(trainer, pokemonId, 1);
                trainer.getObtainedPokemon().add(obtainedPokemon);
            }
        }
        return Optional.ofNullable(converter.toBoosterPackDTO(boosterPack));
    }
}
