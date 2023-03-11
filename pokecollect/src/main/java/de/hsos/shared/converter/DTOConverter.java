package de.hsos.shared.converter;

import de.hsos.entity.pokemon.ObtainedPokemon;
import de.hsos.entity.pokemon.Pokemon;
import de.hsos.entity.pokemon.PokemonType;
import de.hsos.entity.trademarket.Trade;
import de.hsos.entity.user.BoosterPack;
import de.hsos.entity.user.Trainer;
import de.hsos.shared.dto.*;

import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: beryildi, jbelasch
 */

@RequestScoped
public class DTOConverter {

    public TrainerWithLinkDTO toUserWithLinkDTO(Trainer trainer) {
        return new TrainerWithLinkDTO(trainer.getId(), trainer.getUsername());
    }

    public TrainerFullDTO toTrainerFullDTO(Trainer trainer) {
        return new TrainerFullDTO(trainer.getId(), trainer.getUsername(), trainer.getCoinPurse(), this.toObtainedPokemonDTO(trainer.getObtainedPokemon()));
    }

    private List<ObtainedPokemonDTO> toObtainedPokemonDTO(List<ObtainedPokemon> obtainedPokemon) {
        List<ObtainedPokemonDTO> tmp = new ArrayList<>();

        for (ObtainedPokemon o : obtainedPokemon) {
            tmp.add(new ObtainedPokemonDTO(o.getPokemonId(), o.getAmount()));
        }
        return tmp;
    }

    public PokemonWithLinkDTO toPokemonWithLinkDTO(ObtainedPokemon obtainedPokemon) {
        return new PokemonWithLinkDTO(obtainedPokemon.getPokemonId());
    }

    public PokemonWithLinkDTO toPokemonWithLinkDTO(Pokemon pokemon) {
        return new PokemonWithLinkDTO(pokemon.getId());
    }

    public PokemonExistsDTO toPokemonExistDTO(ObtainedPokemon obtainedPokemon) {
        return new PokemonExistsDTO(obtainedPokemon.getPokemonId(), obtainedPokemon.getAmount(), obtainedPokemon.isFavorite());
    }

    public PokemonFullDTO toPokemonFullDTO(Pokemon pokemon) {
        return new PokemonFullDTO(pokemon.getId(), pokemon.getImageUri(), pokemon.getName(), this.toPokemonTypeDTO(pokemon.getPokemonType()));
    }

    public PokemonTypeDTO toPokemonTypeDTO(PokemonType pokemonType) {
        return new PokemonTypeDTO(pokemonType.getPrimaryType(), pokemonType.getSecondaryType());
    }

    public ObtainedPokemonWithLinkDTO toObtainedPokemonWithLinkDTO(ObtainedPokemon obtainedPokemon) {
        return new ObtainedPokemonWithLinkDTO(obtainedPokemon.getPokemonId(), obtainedPokemon.getAmount());
    }

    public BoosterPackDTO toBoosterPackDTO(BoosterPack boosterPack) {
        return new BoosterPackDTO(boosterPack.getPokemonIdList());
    }

    public TradeReturnDTO toTradeReturnDTO(Trade trade) {
        return new TradeReturnDTO(trade.getId(), trade.getTrainerId(), trade.getOfferedPokemonId(), trade.getAcceptablePokemonForTrade().getPokemonForTradeId());
    }

    public TradeWithLinkDTO toTradeWithLinkDTO(Trade trade) {
        return new TradeWithLinkDTO(trade.getId());
    }
}
