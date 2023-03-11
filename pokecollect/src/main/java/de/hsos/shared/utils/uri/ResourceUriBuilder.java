package de.hsos.shared.utils.uri;

import de.hsos.boundry.admin.AdminResource;
import de.hsos.boundry.pokemon.PokemonResource;
import de.hsos.boundry.trademarket.TradeResource;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @Author: rroosmann :), beryildi, jbelasch
 */

@ApplicationScoped
public class ResourceUriBuilder {


    public URI forPokemon(Long id, UriInfo uriInfo) {
        return createResourceUri(PokemonResource.class, "getPokemonById", id, uriInfo);
    }

    public URI forTrainer(Long id, UriInfo uriInfo) {
        return createResourceUri(AdminResource.class, "getUserById", id, uriInfo);
    }

    public URI forTrade(Long id, UriInfo uriInfo) {
        return createResourceUri(TradeResource.class, "getTradeOfferById", id, uriInfo);
    }

    private URI createResourceUri(Class<?> resourceClass, String method, Long id, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(resourceClass).path(resourceClass, method).build(id);
    }

}