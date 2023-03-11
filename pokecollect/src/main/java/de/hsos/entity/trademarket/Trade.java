package de.hsos.entity.trademarket;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

/**
 * @Author: beryildi, jbelasch
 */

@Entity
public class Trade extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "TradeSequence",
            sequenceName = "Trade_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TradeSequence")
    @Column(name = "id", nullable = false)

    private Long id;

    private Long trainerId;

    private Long offeredPokemonId;

    @OneToOne(cascade = CascadeType.ALL)
    AcceptablePokemonForTrade acceptablePokemonForTrade;

    public Trade() {
    }

    public Trade(Long trainerId, Long offeredPokemonId, AcceptablePokemonForTrade acceptablePokemonForTrade) {
        this.trainerId = trainerId;
        this.offeredPokemonId = offeredPokemonId;
        this.acceptablePokemonForTrade = acceptablePokemonForTrade;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOfferedPokemonId() {
        return offeredPokemonId;
    }

    public void setOfferedPokemonId(Long offeredPokemonId) {
        this.offeredPokemonId = offeredPokemonId;
    }

    public AcceptablePokemonForTrade getAcceptablePokemonForTrade() {
        return acceptablePokemonForTrade;
    }

    public void setAcceptablePokemonForTrade(AcceptablePokemonForTrade acceptablePokemonForTrade) {
        this.acceptablePokemonForTrade = acceptablePokemonForTrade;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }
}
