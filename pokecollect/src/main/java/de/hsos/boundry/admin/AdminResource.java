package de.hsos.boundry.admin;

import de.hsos.shared.dto.*;
import de.hsos.shared.utils.interceptor.Loggable;
import de.hsos.shared.utils.uri.ResourceUriBuilder;
import de.hsos.shared.utils.ErrorHandler;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @Author: beryildi, jbelasch
 */
@Path("/admins")
@RequestScoped
@Authenticated
@Transactional
public class AdminResource {

    @Inject
    IAdminManagement adminManagement;

    @Inject
    ResourceUriBuilder uriBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(summary = "Get all available trainers", description = "Returns a collection of all existing trainers")
    @APIResponse(description = "All Trainers", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = TrainerWithLinkDTO.class)))
    @APIResponse(description = "No trainers available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(20000)
    @Fallback(fallbackMethod = "fallbackGetAllUser")
    @CircuitBreaker(delay = 500)
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Loggable
    public Response getAllUser()  {

        @Valid
        Collection<TrainerWithLinkDTO> trainerWithLinkDTOS = this.adminManagement.getAllUsers();

        if (!trainerWithLinkDTOS.isEmpty()) {
            trainerWithLinkDTOS.forEach(this::addSelfLinkUser);
            return Response.ok(trainerWithLinkDTOS).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("No trainer found!"))
                .build();
    }

    @GET
    @Operation(summary = "Get a trainer by ID", description = "Return the trainer with the specified id.")
    @APIResponse(description = "Corresponding trainer", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerFullDTO.class)))
    @APIResponse(description = "Trainer with specified id does not exist", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackGetUserById")
    @CircuitBreaker(delay = 500)
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Loggable
    public Response getUserById(@PathParam("userId") @Min(1) Long userId) {
        Optional<TrainerFullDTO> oTrainerFullDTO = this.adminManagement.getUserById(userId);

        if (oTrainerFullDTO.isPresent()) {
            oTrainerFullDTO.get().obtainedPokemonList.forEach(this::addSelfLinkToPokemon);

            return Response.ok(oTrainerFullDTO.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("User with User ID: " + userId + " was not found!"))
                .build();
    }


    @POST
    @Operation(summary = "Add Pokemon to a trainer", description = "Creates a new Pokemon with the provided data and add it to a trainer.")
    @RequestBody(description = "Create a new Pokemon", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = PokemonLightDTO.class)))
    @APIResponse(description = "Pokemon was added", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonExistsDTO.class)))
    @APIResponse(description = "Error adding Pokemon to trainer", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackAddPokemonToUserById")
    @CircuitBreaker(delay = 500)
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Loggable
    public Response addPokemonToUserById(@PathParam("id") @Min(1) Long userId, @Valid List<PokemonLightDTO> pokemonList) {


        /* Because of the bean validation, this case can no longer occur
        if (pokemonList.stream().anyMatch(pokemon -> pokemon.amount <= 0)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorHandler("Amount of the parsed Pokemon List should not be negative!"))
                    .build();
        }*/

        Optional<PokemonExistsDTO> oPokemonExist = this.adminManagement.addPokemonToUser(userId, pokemonList);
        if (oPokemonExist.isPresent()) {
            return Response.ok(oPokemonExist.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("User with User ID: " + userId + " does not exist!"))
                .build();
    }

    @DELETE
    @Transactional
    @Operation(summary = "Delete a Pokemon from a trainer", description = "Delete Pokemon with the provided data and remove it to from a trainer.")
    @RequestBody(description = "Delete a Pokemon", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = PokemonLightDTO.class)))
    @APIResponse(description = "Pokemon was deleted", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonExistsDTO.class)))
    @APIResponse(description = "Error deleting Pokemon from trainer", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackDeletePokemonFromUserById")
    @CircuitBreaker(delay = 500)
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Loggable
    public Response deletePokemonFromUserById(@PathParam("id") @Min(1) Long userId, @Valid List<PokemonLightDTO> pokemonList) {


        /* Because of the bean validation, this case can no longer occur
        // check if the amount is greater than 0, if yes return 400
        if (pokemonList.stream().anyMatch(pokemon -> pokemon.amount <= 0)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorHandler("Amount of the parsed Pokemon List should not be negative!"))
                    .build();
        }*/

        Optional<PokemonExistsDTO> oPokemonExist = this.adminManagement.deletePokemonFromUser(userId, pokemonList);
        if (oPokemonExist.isPresent()) {
            return Response.ok(oPokemonExist.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("User with User ID: " + userId + " does not exist!"))
                .build();
    }

    private void addSelfLinkUser(TrainerWithLinkDTO trainerWithLinkDTO) {
        URI selfUri = this.uriBuilder.forTrainer(trainerWithLinkDTO.id, this.uriInfo);
        Link link = Link.fromUri(selfUri)
                .type(MediaType.APPLICATION_JSON)
                .build();

        trainerWithLinkDTO.url = link.getUri();
    }

    private void addSelfLinkToPokemon(ObtainedPokemonDTO obtainedPokemonDTO) {
        URI selfUri = this.uriBuilder.forPokemon(obtainedPokemonDTO.pokemonId, this.uriInfo);
        Link link = Link.fromUri(selfUri)
                .type(MediaType.APPLICATION_JSON)
                .build();

        obtainedPokemonDTO.url = link.getUri();
    }


    public Response fallbackGetAllUser() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable, please try again later."))
                .build();
    }

    public Response fallbackGetUserById(Long userId) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable, please try again later."))
                .build();
    }

    public Response fallbackAddPokemonToUserById(Long userId, List<PokemonLightDTO> pokemonList) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable, please try again later."))
                .build();
    }

    public Response fallbackDeletePokemonFromUserById(Long userId, List<PokemonLightDTO> pokemonList) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable, please try again later."))
                .build();
    }

}
