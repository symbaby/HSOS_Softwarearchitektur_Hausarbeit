package de.hsos.boundry.trademarket;

import de.hsos.shared.dto.PokemonFullDTO;
import de.hsos.shared.dto.PokemonTradeDTO;
import de.hsos.shared.dto.TradeReturnDTO;
import de.hsos.shared.dto.TradeWithLinkDTO;
import de.hsos.shared.utils.interceptor.Loggable;
import de.hsos.shared.utils.uri.ResourceUriBuilder;
import de.hsos.shared.utils.ErrorHandler;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
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
import java.util.Optional;


/**
 * @Author: beryildi, jbelasch
 */
@Path("/trade")
@RequestScoped
@Authenticated
@Transactional
public class TradeResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    ITradeManager tradeManager;

    @Inject
    ResourceUriBuilder uriBuilder;

    @Context
    UriInfo uriInfo;

    @POST
    @Operation(summary = "Create a trade offer", description = "Creates a new trade offer with the provided data.")
    @RequestBody(description = "Create a new Trade offer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PokemonTradeDTO.class)))
    @APIResponse(description = "Trade offer that was created", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeReturnDTO.class)))
    @APIResponse(description = "Error creating trade offer", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(500)
    @Fallback(fallbackMethod = "fallbackOfferTrade")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Loggable
    public Response offerTrade(@Valid PokemonTradeDTO pokemonTradeDTO) {

        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<TradeReturnDTO> oTradeReturnDTO = this.tradeManager.offerTrade(trainerName, pokemonTradeDTO);

        if (oTradeReturnDTO.isPresent()) {
            return Response.ok(oTradeReturnDTO).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Tradeoff was rejected. You dont have the offered Pokemon!"))
                .build();
    }


    @GET
    @Operation(summary = "Get all available trade offers", description = "Returns a collection of all existing trade offers except the self created ones.")
    @APIResponse(description = "All trade offers except self created ones", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeWithLinkDTO.class)))
    @APIResponse(description = "No trade offer available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(20000)
    @Fallback(fallbackMethod = "fallbackGetAllTradeOffer")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Path("/allOffers")
    @Loggable
    public Response getAllTradeOffer() {

        String trainerName = securityIdentity.getPrincipal().getName();
        Collection<TradeWithLinkDTO> tradeOffers = this.tradeManager.showOffers(trainerName);

        tradeOffers.forEach(this::addSelfLinkToTrade);
        return Response.ok(tradeOffers).build();

    }

    @GET
    @Operation(summary = "Get self created trade offers", description = "Returns a collection of self created trade offers.")
    @APIResponse(description = "All trade offers that are self created", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeWithLinkDTO.class)))
    @APIResponse(description = "No trade offer available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(20000)
    @Fallback(fallbackMethod = "fallbackGetMyTradeOffer")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Path("/myOffers")
    @Loggable
    public Response getMyTradeOffer() {

        String trainerName = securityIdentity.getPrincipal().getName();
        Collection<TradeWithLinkDTO> myTradeOffers = this.tradeManager.showMyOffers(trainerName);

        myTradeOffers.forEach(this::addSelfLinkToTrade);
        return Response.ok(myTradeOffers).build();

    }

    @GET
    @Operation(summary = "Get trade offer by ID", description = "Returns a trade offers with the specified id.")
    @APIResponse(description = "Corresponding trade offers", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeReturnDTO.class)))
    @APIResponse(description = "No trade offer available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackGetTradeOfferById")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Path("/offer/{offerId}")
    @Loggable
    public Response getTradeOfferById(@PathParam("offerId") @Min(1) Long offerId) {

        Optional<TradeReturnDTO> oTradeReturn = this.tradeManager.getTradeOfferById(offerId);

        if (oTradeReturn.isPresent()) {
            return Response.ok(oTradeReturn).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Trade with Trade ID: " + offerId + " does not exits!"))
                .build();
    }

    @DELETE
    @Operation(summary = "Delete a trade offer by ID", description = "Delete a trade offers with the specified id.")
    @APIResponse(description = "Delete the corresponding trade offers", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeReturnDTO.class)))
    @APIResponse(description = "No trade offer available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackDeleteMyTradeOffer")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Path("/{tradeId}")
    @Loggable
    public Response deleteMyTradeOffer(@PathParam("tradeId") @Min(1) Long tradeId) {

        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<TradeReturnDTO> oTradeDTO = this.tradeManager.deleteMyTradeOffer(trainerName, tradeId);

        if (oTradeDTO.isPresent()) {
            return Response.ok(oTradeDTO).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Deletion of trade was rejected because trade id: " + tradeId + " does not exist!"))
                .build();
    }


    @GET
    @Operation(summary = "Complete trade offers transaction", description = "Complete trade offers transaction with the specified id.")
    @APIResponse(description = "Complete trade offers transaction", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeReturnDTO.class)))
    @APIResponse(description = "No trade offer available", responseCode = "404")
    @Retry(maxRetries = 5, delay = 1000)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackCompleteTransaction")
    @CircuitBreaker(delay = 500)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Path("/{tradeId}")
    @Loggable
    public Response completeTransaction(@PathParam("tradeId") @Min(1) Long tradeId) {

        String trainerName = securityIdentity.getPrincipal().getName();
        Optional<TradeReturnDTO> oTradeDTO = this.tradeManager.completeTransaction(trainerName, tradeId);

        if (oTradeDTO.isPresent()) {
            return Response.ok(oTradeDTO).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorHandler("Trade was rejected because trade id: " + tradeId + " does not exist!"))
                .build();
    }

    private void addSelfLinkToTrade(TradeWithLinkDTO tradeWithLinkDTO) {

        URI selfUri = this.uriBuilder.forTrade(tradeWithLinkDTO.tradeId, this.uriInfo);
        Link link = Link.fromUri(selfUri)
                .type(MediaType.APPLICATION_JSON)
                .build();

        tradeWithLinkDTO.url = link.getUri();
    }


    public Response fallbackOfferTrade(PokemonTradeDTO pokemonTradeDTO) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetAllTradeOffer() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackGetMyTradeOffer() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }


    public Response fallbackGetTradeOfferById(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackDeleteMyTradeOffer(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackCompleteTransaction(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackCreateTrade(PokemonFullDTO pokemonFullDTO) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackUpdateTrade(Long id, PokemonFullDTO pokemonFullDTO) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

    public Response fallbackDeleteTrade(Long id) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorHandler("The service is currently unavailable. Please try again later."))
                .build();
    }

}
