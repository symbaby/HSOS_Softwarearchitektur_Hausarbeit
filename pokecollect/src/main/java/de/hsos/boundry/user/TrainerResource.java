package de.hsos.boundry.user;

import de.hsos.shared.dto.*;
import de.hsos.shared.utils.interceptor.Loggable;
import de.hsos.shared.utils.uri.ResourceUriBuilder;
import de.hsos.shared.utils.ConfigSetting;
import de.hsos.shared.utils.ErrorHandler;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

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
import java.util.List;
import java.util.Optional;


/**
 * @Author: beryildi, jbelasch
 */
@Path("/users")
@Transactional
@RequestScoped
public class TrainerResource {

    @Inject
    IUserManagement userManagement;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    ResourceUriBuilder uriBuilder;

    @Context
    UriInfo uriInfo;


    @GET
    @Operation(summary = "Return the current User", description = "Returns the current logged in User")
    @APIResponse(description = "Logged in User", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrincipalDTO.class)))
    @APIResponse(description = "User is not logged in", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(1000)
    @Fallback(fallbackMethod = "fallbackUserData")
    @CircuitBreaker(delay = 500)
    @Path("/whoami")
    @RolesAllowed({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public Response userData() {
        String trainerName = securityIdentity.getPrincipal().getName();

        Optional<PrincipalDTO> userData = this.userManagement.getUserData(trainerName);

        if (userData.isPresent()) {
            return Response.ok(userData).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Something went wrong!"))
                .build();

    }

    @GET
    @Operation(summary = "Get all obtained Pokemon ", description = "Returns all obtained Pokemon from Trainer")
    @APIResponse(description = "All Obtained Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ObtainedPokemonWithLinkDTO.class)))
    @APIResponse(description = "No obtained Pokemon available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(20000)
    @Fallback(fallbackMethod = "fallbackGetAllPokemon")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Loggable
    public Response getAllPokemon() {
        String trainerName = securityIdentity.getPrincipal().getName();
        Collection<ObtainedPokemonWithLinkDTO> obtainedPokemonWithLinkDTOS = this.userManagement.getAllPokemon(trainerName);

        if (!obtainedPokemonWithLinkDTOS.isEmpty()) {
            obtainedPokemonWithLinkDTOS.forEach(this::addSelfLinkToObtainedPokemon);
            return Response.ok(obtainedPokemonWithLinkDTOS).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Something went wrong!"))
                .build();

    }

    @GET
    @Operation(summary = "Get a Pokemon by ID", description = "Return the obtained Pokemon with the specified id.")
    @APIResponse(description = "Corresponding Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonExistsDTO.class)))
    @APIResponse(description = "Pokemon with specified id does not exist", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackGetPokemonById")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Path("/{pokemonId}")
    @Loggable
    public Response getPokemonById(@PathParam("pokemonId") @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON) Long pokemonId) {

        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<PokemonExistsDTO> pokemonExistsDTO = this.userManagement.getPokemonById(pokemonId, trainerName);

        if (pokemonExistsDTO.isPresent()) {
            return Response.ok(pokemonExistsDTO).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Operation(summary = "Get all favorite Pokemon", description = "Returns a collection of all Pokemon that are set as favorite.")
    @APIResponse(description = "All favorite Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = PokemonWithLinkDTO.class)))
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(500)
    @Fallback(fallbackMethod = "fallbackGetFavoritePokemon")
    @CircuitBreaker(delay = 500)
    @Path("/favorite")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public Response getFavoritePokemon() {
        String trainerName = securityIdentity.getPrincipal().getName();

        Collection<PokemonWithLinkDTO> pokemonWithLinkDTOS = this.userManagement.getFavoritePokemon(trainerName);
        pokemonWithLinkDTOS.forEach(this::addSelfLinkToPokemon);
        return Response.ok(pokemonWithLinkDTOS).build();
    }

    @PUT
    @Operation(summary = "Update favorite Status of Pokemon", description = "Updates the favorite status of the Pokemon with the specified id.")
    @APIResponse(description = "Updated corresponding Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonWithLinkDTO.class)))
    @APIResponse(description = "Pokemon with specified id does not exist", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackSetFavoritePokemon")
    @CircuitBreaker(delay = 500)
    @Path("/favorite/{pokemonId}")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Loggable
    public Response setFavoritePokemon(@PathParam("pokemonId") @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON) Long pokemonId) {
        String trainerName = securityIdentity.getPrincipal().getName();

        Optional<PokemonWithLinkDTO> oPokemonWithLinkDTO = this.userManagement.setFavoritePokemon(pokemonId, trainerName);
        if (oPokemonWithLinkDTO.isPresent()) {
            this.addSelfLinkToPokemon(oPokemonWithLinkDTO.get());
            return Response.ok(oPokemonWithLinkDTO).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Favorite Pokemon cannot be set because Pokemon ID with " + pokemonId + " does not exist"))
                .build();
    }

    @DELETE
    @Operation(summary = "Removes favorite Status of Pokemon", description = "Removes the favorite status of the Pokemon with the specified id.")
    @APIResponse(description = "Removes corresponding Pokemon", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonWithLinkDTO.class)))
    @APIResponse(description = "Pokemon with specified id does not exist", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackRemoveFromFavorites")
    @CircuitBreaker(delay = 500)
    @Path("/favorite/{pokemonId}")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Loggable
    public Response removeFromFavorites(@PathParam("pokemonId") @Min(ConfigSetting.MIN_POKEMON) @Max(ConfigSetting.MAX_POKEMON) Long pokemonId) {
        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<PokemonWithLinkDTO> oPokemonWithLinkDTO = this.userManagement.removeFavoritePokemon(pokemonId, trainerName);

        if (oPokemonWithLinkDTO.isPresent()) {
            this.addSelfLinkToPokemon(oPokemonWithLinkDTO.get());
            return Response.ok(oPokemonWithLinkDTO).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Favorite Pokemon cannot be removed because Pokemon ID with " + pokemonId + " does not exist\""))
                .build();
    }


    @GET
    @Operation(summary = "Open a free booster pack", description = "Open a free booster pack and add the new Pokemon to the obtained Pokemon list.")
    @APIResponse(description = "Open a free booster Pack", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoosterPackDTO.class)))
    @APIResponse(description = "Not enough time passed to unbox a booster pack", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackObtainFreeBooster")
    @CircuitBreaker(delay = 500)
    @Path("/freeBooster")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public Response obtainFreeBooster() {
        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<BoosterPackDTO> oBoosterPackDTO = this.userManagement.obtainFreeBooster(trainerName);
        if (oBoosterPackDTO.isPresent()) {
            return Response.ok(oBoosterPackDTO.get()).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Operation(summary = "Open a booster pack", description = "Open a booster pack and add the new Pokemon to the obtained Pokemon list.")
    @APIResponse(description = "Open a booster Pack", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoosterPackDTO.class)))
    @APIResponse(description = "Not enough money to unbox a booster pack", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackBuyBooster")
    @CircuitBreaker(delay = 500)
    @Path("/buyBooster")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public Response buyBooster() {
        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<BoosterPackDTO> oBoosterPackDTO = this.userManagement.buyBooster(trainerName);
        if (oBoosterPackDTO.isPresent()) {
            return Response.ok(oBoosterPackDTO.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("You need more coins to obtain a new BoosterPack"))
                .build();
    }

    @POST
    @Operation(summary = "Create a new User", description = "Create a new User and persist it as a trainer")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Path("/register")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Fallback(fallbackMethod = "fallbackCreateNewUser")
    @Loggable
    // Src: https://gist.github.com/thomasdarimont/c4e739c5a319cf78a4cff3b87173a84b
    public Response createNewUser(@QueryParam("username") String username_, @QueryParam("password") String password_) {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8180")
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();

        // Create user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username_);
        user.setEnabled(true);

        // Get realm
        String realmName = "quarkus";
        RealmResource keycloakRealmResource = keycloak.realm(realmName);
        UsersResource keycloakUserResource = keycloakRealmResource.users();

        // Create user (requires manage-users role)
        Response response = keycloakUserResource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);

        // Define password credential
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password_);

        UserResource userResource = keycloakUserResource.get(userId);
        // Set password credential
        userResource.resetPassword(passwordCred);


        // Get realm role "user" (requires view-realm role)
        RoleRepresentation userRealmRole = keycloakRealmResource.roles().get("user").toRepresentation();

        // Assign realm role "user" to user
        userResource.roles().realmLevel().add(List.of(userRealmRole));


        /* We dont need the other configs here
        // Get client
        // String clientName = "admin-cli";
        // ClientRepresentation appClient = keycloakRealmResource.clients().findByClientId(clientName).get(0);


        System.out.println("7");


        // Get client level role (requires view-clients role)
        // RoleRepresentation userClientRole = keycloakRealmResource.clients().get(appClient.getId()).roles().get("user").toRepresentation();

        System.out.println("8");


        // Assign client level role to user
        // userResource.roles().clientLevel(appClient.getId()).add(List.of(userClientRole));


        System.out.println("9");
        */


        // ToDo: Create a Trainer with the name and persist it in the Database
        Optional<TrainerFullDTO> oTrainerFullDTO = this.userManagement.registerNewUser(username_);

        if (oTrainerFullDTO.isPresent()) {
            return Response.ok(oTrainerFullDTO).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private void addSelfLinkToPokemon(PokemonWithLinkDTO pokemonWithLinkDTO) {
        URI selfUri = this.uriBuilder.forPokemon(pokemonWithLinkDTO.pokemonId, this.uriInfo);
        Link link = Link.fromUri(selfUri)
                .type(MediaType.APPLICATION_JSON)
                .build();

        pokemonWithLinkDTO.url = link.getUri();
    }

    private void addSelfLinkToObtainedPokemon(ObtainedPokemonWithLinkDTO obtainedPokemonWithLinkDTO) {
        URI selfUri = this.uriBuilder.forPokemon(obtainedPokemonWithLinkDTO.pokemonId, this.uriInfo);
        Link link = Link.fromUri(selfUri)
                .type(MediaType.APPLICATION_JSON)
                .build();

        obtainedPokemonWithLinkDTO.url = link.getUri();
    }


    public Response fallbackUserData() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetAllPokemon() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetPokemonById(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetFavoritePokemon() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackSetFavoritePokemon(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackRemoveFromFavorites(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackObtainFreeBooster() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackBuyBooster() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackCreateNewUser(String username_, String password_) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

}