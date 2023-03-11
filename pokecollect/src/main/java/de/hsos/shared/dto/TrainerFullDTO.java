package de.hsos.shared.dto;

import de.hsos.entity.user.CoinPurse;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: beryildi, jbelasch
 */
public class TrainerFullDTO {

    public Long id;
    public String name;
    public CoinPurse coinPurse;
    public List<ObtainedPokemonDTO> obtainedPokemonList;

    public TrainerFullDTO(){}

    public TrainerFullDTO(Long id, String name, CoinPurse coinPurse, List<ObtainedPokemonDTO> obtainedPokemonList) {
        this.id = id;
        this.name = name;
        this.coinPurse = coinPurse;
        this.obtainedPokemonList = new ArrayList<>();

        // stream through the list and add each element to the new list
        this.obtainedPokemonList.addAll(obtainedPokemonList);
        
    }

}
