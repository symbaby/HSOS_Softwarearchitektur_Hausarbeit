package de.hsos.boundry.pokemon;


import de.hsos.shared.dto.PokemonFullDTO;
import de.hsos.shared.dto.PokemonWithLinkDTO;
import de.hsos.shared.utils.interceptor.Loggable;
import de.hsos.shared.utils.uri.ResourceUriBuilder;
import de.hsos.shared.utils.ConfigSetting;
import de.hsos.shared.utils.ErrorHandler;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

/**
 * @Author: beryildi, jbelasch
 */

@Path("/pokemon")
@RequestScoped
@Transactional
public class PokemonResource {

    @Inject
    IPokemonManagement pokemonManagement;

    @Inject
    ResourceUriBuilder uriBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(summary = "Get all available Pokemon", description = "Returns a collection of all existing Pokemon.")
    @APIResponse(description = "All Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonWithLinkDTO.class)))
    @APIResponse(description = "No Pokemon available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(30000)
    @Fallback(fallbackMethod = "fallbackGetAllPokemon")
    @CircuitBreaker(delay = 500)
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Loggable
    public Response getAllPokemon() {

        Collection<PokemonWithLinkDTO> oPokemonFullDTOS = this.pokemonManagement.getAllPokemon();
        oPokemonFullDTOS.forEach(this::addSelfLinkToPokemon);

        return Response.ok(oPokemonFullDTOS).build();
    }

    @GET
    @Operation(summary = "Get all available Pokemon with Paging", description = "Returns a collection of all existing Pokemon with Paging.")
    @APIResponse(description = "All Pokemon with Paging", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonWithLinkDTO.class)))
    @APIResponse(description = "No Pokemon available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(10000)
    @Fallback(fallbackMethod = "fallbackGetPokemonWithPaging")
    @CircuitBreaker(delay = 500)
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/page/{pageIndex}")
    @Loggable
    public Response getAllPokemonWithPaging(@PathParam("pageIndex") @Min(0) Integer pageIndex) {

        Collection<PokemonWithLinkDTO> oPokemonFullDTOS = this.pokemonManagement.getAllPokemonWithPaging(pageIndex);
        oPokemonFullDTOS.forEach(this::addSelfLinkToPokemon);


        return Response.ok(oPokemonFullDTOS).build();

    }

    @GET
    @Operation(summary = "Get a Pokemon by Id", description = "Returns the Pokemon with the specified id.")
    @APIResponse(description = "Corresponding Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonFullDTO.class)))
    @APIResponse(description = "Pokemon with specified id does not exist", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackGetPokemonById")
    @CircuitBreaker(delay = 500)
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{pokemonId}")
    @Loggable
    public Response getPokemonById(@PathParam("pokemonId") @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON) Long pokemonId) {

        Optional<PokemonFullDTO> oPokemonFullDTO = this.pokemonManagement.getPokemonById(pokemonId);

        if (oPokemonFullDTO.isPresent()) {
            return Response.ok(oPokemonFullDTO.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Pokemon with Pokemon ID: " + pokemonId + " does not exist!"))
                .build();

    }

    @GET
    @Operation(summary = "Initialize Database", description = "Initialize Database and fill it with Pokemon.")
    @APIResponse(description = "Initialization is successful", responseCode = "200")
    @APIResponse(description = "Initialization is not successful", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(30000)
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Path("/initDB")
    @Loggable
    public Response initDB() {
        this.pokemonManagement.initDB();
        return Response.ok().build();
    }


    private void addSelfLinkToPokemon(PokemonWithLinkDTO pokemonWithLinkDTO) {
        URI selfUri = this.uriBuilder.forPokemon(pokemonWithLinkDTO.pokemonId, this.uriInfo);
        Link link = Link.fromUri(selfUri)
                .type(MediaType.APPLICATION_JSON)
                .build();

        pokemonWithLinkDTO.url = link.getUri();
    }

    public Response fallbackGetAllPokemon() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetPokemonWithPaging(Integer id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetPokemonById(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

}
