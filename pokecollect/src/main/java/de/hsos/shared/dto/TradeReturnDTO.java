package de.hsos.shared.dto;

/**
 * @Author: beryildi, jbelasch
 */
public class TradeReturnDTO {
    public Long tradeId;
    public Long trainerId;
    public Long offeredPokemonId;
    public Long acceptablePokemonId;

    public TradeReturnDTO() {
    }

    public TradeReturnDTO(Long tradeId, Long trainerId, Long offeredPokemonId, Long acceptablePokemonId) {
        this.tradeId = tradeId;
        this.trainerId = trainerId;
        this.offeredPokemonId = offeredPokemonId;
        this.acceptablePokemonId = acceptablePokemonId;
    }


}
