package de.hsos.acl.gateway;


import de.hsos.acl.pokemonDTOfromAPI.PokemonDTOFromAPI;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @Author: beryildi, jbelasch
 */
@Path("/pokemon")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "pokemonAPI")
public interface PokemonGateway {

    @GET
    @Path("/{pokemonId}")
    PokemonDTOFromAPI getPokemonById(@PathParam("pokemonId") Long pokemonId);
}

