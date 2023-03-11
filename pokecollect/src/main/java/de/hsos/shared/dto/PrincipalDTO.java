package de.hsos.shared.dto;

/**
 * @Author: beryildi, jbelasch
 */
public class PrincipalDTO {
    public String name;
    public int coinAmount;


    public PrincipalDTO(String name, int coinAmount){
        this.name = name;
        this.coinAmount = coinAmount;
    }

    public PrincipalDTO(){}
}
