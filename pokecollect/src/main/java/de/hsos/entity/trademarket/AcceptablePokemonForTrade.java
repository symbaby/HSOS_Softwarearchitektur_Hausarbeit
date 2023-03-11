package de.hsos.entity.trademarket;

import javax.persistence.*;

/**
 * @Author: beryildi, jbelasch
 */

@Entity
public class AcceptablePokemonForTrade {
    @Id
    @SequenceGenerator(
            name = "AcceptablePokemonForTrade",
            sequenceName = "AcceptablePokemonForTrade_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AcceptablePokemonForTrade")
    private Long id;

    private Long pokemonForTradeId;

    public AcceptablePokemonForTrade() {
    }

    public AcceptablePokemonForTrade(Long pokemonForTradeId) {
        this.pokemonForTradeId = pokemonForTradeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPokemonForTradeId() {
        return pokemonForTradeId;
    }

    public void setPokemonForTradeId(Long pokemonForTradeId) {
        this.pokemonForTradeId = pokemonForTradeId;
    }
}
