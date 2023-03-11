package de.hsos.entity.user;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

/**
 * @Author: beryildi, jbelasch
 */
@Entity
public class CoinPurse extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "CoinPurseSequence",
            sequenceName = "CoinPurse_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CoinPurseSequence")
    @Column(name = "id", nullable = false)
    private Long id;

    private int balance;

    public CoinPurse() {
    }

    public CoinPurse(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}
