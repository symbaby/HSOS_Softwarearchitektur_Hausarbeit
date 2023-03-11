package de.hsos.control.trademarket;

import de.hsos.boundry.trademarket.ITradeManager;
import de.hsos.entity.catalogue.ITradeCatalogue;
import de.hsos.shared.dto.PokemonTradeDTO;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import de.hsos.shared.dto.TradeReturnDTO;
import de.hsos.shared.dto.TradeWithLinkDTO;

import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
@RequestScoped
public class TradeManager implements ITradeManager {

    @Inject
    ITradeCatalogue tradeCatalogue;

    @Override
    public Optional<TradeReturnDTO> offerTrade(String trainerName, PokemonTradeDTO pokemonTradeDTO) {
        return this.tradeCatalogue.offerTrade(trainerName, pokemonTradeDTO);
    }

    @Override
    public Collection<TradeWithLinkDTO> showOffers(String trainerName) {
        return this.tradeCatalogue.showOffers(trainerName);
    }

    @Override
    public Collection<TradeWithLinkDTO> showMyOffers(String trainerName) {
        return this.tradeCatalogue.showMyOffers(trainerName);
    }

    @Override
    public Optional<TradeReturnDTO> getTradeOfferById(Long tradeId) {
        return this.tradeCatalogue.getTradeOfferById(tradeId);
    }

    @Override
    public Optional<TradeReturnDTO> deleteMyTradeOffer(String trainerName, Long tradeId) {
        return this.tradeCatalogue.deleteMyTradeOffer(trainerName, tradeId);
    }

    @Override
    public Optional<TradeReturnDTO> completeTransaction(String trainerName, Long tradeId) {
        return this.tradeCatalogue.completeTransaction(trainerName, tradeId);
    }
}
