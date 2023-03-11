package de.hsos.shared.dto;

import java.net.URI;

/**
 * @Author: beryildi, jbelasch
 */
public class TradeWithLinkDTO {
    public Long tradeId;
    public URI url;

    public TradeWithLinkDTO() {
    }

    public TradeWithLinkDTO(Long tradeId) {
        this.tradeId = tradeId;
    }
}
