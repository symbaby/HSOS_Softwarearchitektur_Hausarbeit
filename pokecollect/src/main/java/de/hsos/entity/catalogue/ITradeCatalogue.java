package de.hsos.entity.catalogue;

import de.hsos.shared.dto.PokemonTradeDTO;
import de.hsos.shared.dto.TradeReturnDTO;
import de.hsos.shared.dto.TradeWithLinkDTO;

import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */
public interface ITradeCatalogue {

    Optional<TradeReturnDTO> offerTrade(String trainerName, PokemonTradeDTO pokemonTradeDTO);

    Collection<TradeWithLinkDTO> showOffers(String trainerName);

    Collection<TradeWithLinkDTO> showMyOffers(String trainerName);

    Optional<TradeReturnDTO> getTradeOfferById(Long tradeId);

    Optional<TradeReturnDTO> deleteMyTradeOffer(String trainerName, Long tradeId);

    Optional<TradeReturnDTO> completeTransaction(String trainerName, Long tradeId);
}
