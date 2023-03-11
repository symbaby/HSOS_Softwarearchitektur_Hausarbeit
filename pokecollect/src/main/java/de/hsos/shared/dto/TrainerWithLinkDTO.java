package de.hsos.shared.dto;

import java.net.URI;

/**
 * @Author: beryildi, jbelasch
 */
public class TrainerWithLinkDTO {

    public Long id;
    public String name;
    public URI url;

    public TrainerWithLinkDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TrainerWithLinkDTO() {
    }
}

