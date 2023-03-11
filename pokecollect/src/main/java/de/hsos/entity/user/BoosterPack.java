package de.hsos.entity.user;

import de.hsos.shared.utils.ConfigSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author: beryildi, jbelasch
 */

public class BoosterPack {

    private double price;
    private List<Long> pokemonIdList;

    public BoosterPack() {
        this.pokemonIdList = getRandomNumbers(ConfigSetting.BOOSTER_START_RANGE, ConfigSetting.BOOSTER_END_RANGE);
    }

    private ArrayList<Long> getRandomNumbers(int min, int max) {
        ArrayList<Long> randomNumbers = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            Long randomNum = rand.nextLong((max - min) + 1) + min;
            randomNumbers.add(randomNum);
        }
        return randomNumbers;
    }

    public BoosterPack(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Long> getPokemonIdList() {
        return pokemonIdList;
    }

    public void setPokemonIdList(List<Long> pokemonIdList) {
        this.pokemonIdList = pokemonIdList;
    }

    @Override
    public String toString() {
        return "BoosterPack{" +
                "pokemonList=" + pokemonIdList +
                '}';
    }
}
