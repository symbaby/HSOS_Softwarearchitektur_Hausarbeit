package de.hsos.gateway.trademarket;

import de.hsos.entity.catalogue.ITradeCatalogue;
import de.hsos.entity.pokemon.ObtainedPokemon;
import de.hsos.entity.trademarket.AcceptablePokemonForTrade;
import de.hsos.entity.trademarket.Trade;
import de.hsos.entity.user.Trainer;
import de.hsos.shared.converter.DTOConverter;
import de.hsos.shared.dto.PokemonTradeDTO;
import de.hsos.shared.dto.TradeWithLinkDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import de.hsos.shared.dto.TradeReturnDTO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: beryildi, jbelasch
 */

@RequestScoped
public class TradeRepository implements PanacheRepository<Trade>, ITradeCatalogue {

    @Inject
    DTOConverter converter;

    @Override
    public Optional<TradeReturnDTO> offerTrade(String trainerName, PokemonTradeDTO pokemonTradeDTO) {

        Optional<Trainer> oTrainer = this.getTrainer(trainerName);
        if (oTrainer.isPresent()) {
            Optional<ObtainedPokemon> obtainedPokemon = this.getObtainedPokemon(oTrainer.get(), pokemonTradeDTO.offeredPokemonId);
            if (obtainedPokemon.isPresent()) {
                Trade trade = new Trade(oTrainer.get().getId(), pokemonTradeDTO.offeredPokemonId, new AcceptablePokemonForTrade(pokemonTradeDTO.acceptedPokemonId));
                persist(trade);

                if (obtainedPokemon.get().getAmount() - 1 < 1) {
                    oTrainer.get().getObtainedPokemon().remove(obtainedPokemon.get());
                } else {
                    obtainedPokemon.get().setAmount(obtainedPokemon.get().getAmount() - 1);
                }

                return Optional.ofNullable(converter.toTradeReturnDTO(trade));
            }
        }

        return Optional.empty();
    }

    @Override
    public Collection<TradeWithLinkDTO> showOffers(String trainerName) {

        Optional<Trainer> oTrainer = this.getTrainer(trainerName);
        List<Trade> oTrade = listAll();

        return oTrainer.map(trainer -> oTrade.stream().
                filter(ot -> !Objects.equals(ot.getTrainerId(), trainer.getId()))
                .map(converter::toTradeWithLinkDTO)
                .collect(Collectors.toList())).orElse(null);

    }

    @Override
    public Collection<TradeWithLinkDTO> showMyOffers(String trainerName) {
        Optional<Trainer> oTrainer = this.getTrainer(trainerName);
        List<Trade> oTrade = listAll();

        return oTrainer.map(trainer -> oTrade.stream()
                        .filter(ot -> ot.getTrainerId().equals(trainer.getId()))
                        .map(converter::toTradeWithLinkDTO)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    @Override
    public Optional<TradeReturnDTO> getTradeOfferById(Long tradeId) {

        Optional<Trade> oTrade = Optional.ofNullable(findById(tradeId));

        if (oTrade.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(converter.toTradeReturnDTO(oTrade.get()));
        }
    }

    @Override
    public Optional<TradeReturnDTO> deleteMyTradeOffer(String trainerName, Long tradeId) {
        Optional<Trainer> oTrainer = this.getTrainer(trainerName);
        Optional<Trade> oTrade = findByIdOptional(tradeId);

        if (oTrainer.isPresent() && oTrade.isPresent()) {
            if (oTrainer.get().getId().equals(oTrade.get().getTrainerId())) {
                Optional<TradeReturnDTO> tmp = Optional.ofNullable(converter.toTradeReturnDTO(oTrade.get()));

                // Add Pokemon back to Collection if the TradeOffer is deleted
                this.addPokemon(oTrainer.get(), oTrade.get().getOfferedPokemonId());
                delete(oTrade.get());

                return tmp;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TradeReturnDTO> completeTransaction(String trainerName, Long tradeId) {

        Optional<Trade> oTrade = findByIdOptional(tradeId);

        if (oTrade.isPresent()) {
            Optional<Trainer> oBuyer = this.getTrainer(trainerName);
            Optional<Trainer> oSeller = Trainer.findByIdOptional(oTrade.get().getTrainerId());

            if (checkPokemon(oBuyer.get(), oTrade.get().getAcceptablePokemonForTrade().getPokemonForTradeId())) {
                this.addPokemon(oSeller.get(), oTrade.get().getAcceptablePokemonForTrade().getPokemonForTradeId());
                this.removePokemon(oBuyer.get(), oTrade.get().getAcceptablePokemonForTrade().getPokemonForTradeId());
                this.addPokemon(oBuyer.get(), oTrade.get().getOfferedPokemonId());

                delete(oTrade.get());

                return Optional.ofNullable(converter.toTradeReturnDTO(oTrade.get()));
            }
        }
        return Optional.empty();
    }


    private Optional<Trainer> getTrainer(String trainerName) {
        List<Trainer> trainerList = Trainer.listAll();

        return trainerList.stream()
                .filter(trainer -> trainer.getUsername().equals(trainerName))
                .findFirst();
    }

    private boolean checkPokemon(Trainer trainer, Long pokemonIdToCheck) {

        return trainer.getObtainedPokemon()
                .stream()
                .anyMatch(obtainedPokemon -> obtainedPokemon.getPokemonId().equals(pokemonIdToCheck));
    }

    private void addPokemon(Trainer trainer, Long pokemonId) {
        Optional<ObtainedPokemon> obtainedPokemon = this.getObtainedPokemon(trainer, pokemonId);

        if (obtainedPokemon.isPresent()) {
            obtainedPokemon.get().increaseAmount(1);
        } else {
            ObtainedPokemon o = new ObtainedPokemon(trainer, pokemonId, 1);
            trainer.getObtainedPokemon().add(o);
        }
    }

    private void removePokemon(Trainer trainer, Long pokemonId) {
        Optional<ObtainedPokemon> obtainedPokemon = this.getObtainedPokemon(trainer, pokemonId);

        if (obtainedPokemon.isPresent()) {
            if (obtainedPokemon.get().getAmount() <= 1) {
                trainer.getObtainedPokemon().remove(obtainedPokemon.get());
            } else {
                obtainedPokemon.get().decreaseAmount(1);
            }
        }
    }

    private Optional<ObtainedPokemon> getObtainedPokemon(Trainer trainer, Long pokemonId) {

        return trainer.getObtainedPokemon().stream()
                .filter(op -> op.getPokemonId().equals(pokemonId))
                .findFirst();
    }
}
