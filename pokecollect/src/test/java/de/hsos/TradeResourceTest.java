package de.hsos;

import de.hsos.shared.dto.PokemonTradeDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;


import static io.restassured.RestAssured.given;

@QuarkusTest
public class TradeResourceTest {


    // Src: https://umberto.xyz/post/2021-01-06-restassured-get-that-token/ and adjusted with insomnia keycloak video
    public String getAuthToken(String username, String password) {
        Response loginResponse = given()
                .auth()
                .preemptive()
                .basic("frontend-ionic", "perm")
                .formParam("username", username)
                .formParam("password", password)
                .formParam("grant_type", "password")
                .formParam("scope", "openid")
                .when()
                .post("http://localhost:8180/realms/quarkus/protocol/openid-connect/token")
                .then()
                .extract()
                .response();

        return loginResponse.path("access_token");
    }

    @Test
    // Einzeln laufen lassen wegen der Datenbank
    public void testOfferTrade() {

        String authToken = this.getAuthToken("berkan", "berkan");

        PokemonTradeDTO tradeDTO = new PokemonTradeDTO(1L, 10L);
        Jsonb json = JsonbBuilder.create();
        String result = json.toJson(tradeDTO);

        // Create
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .body(result)
                .post("/trade")
                .then()
                .statusCode(200);


        // GetAll
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/trade/myOffers")
                .then()
                .statusCode(200);


        // Delete
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/trade/1")
                .then()
                .statusCode(200);
    }

    @Test
    // Einzeln laufen lassen wegen der Datenbank
    public void testGetOffersFromOtherUser() {

        // Credentials
        String berkanAuthToken = this.getAuthToken("berkan", "berkan");
        String johannesAuthToken = this.getAuthToken("johannes", "password");

        // Dummy Data
        PokemonTradeDTO tradeDTOBerkan = new PokemonTradeDTO(2L, 9L);
        Jsonb jsonBerkan = JsonbBuilder.create();
        String resultBerkan = jsonBerkan.toJson(tradeDTOBerkan);

        // Create
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + berkanAuthToken)
                .when()
                .body(resultBerkan)
                .post("/trade")
                .then()
                .statusCode(200);

        // Dummy Data
        PokemonTradeDTO tradeDTOJohannes = new PokemonTradeDTO(9L, 2L);
        Jsonb jsonJohannes = JsonbBuilder.create();
        String resultJohannes = jsonJohannes.toJson(tradeDTOJohannes);

        // Create
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + johannesAuthToken)
                .when()
                .body(resultJohannes)
                .post("/trade")
                .then()
                .statusCode(200);


        // Show other offers
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + johannesAuthToken)
                .when()
                .get("/trade/allOffers")
                .then()
                .statusCode(200);


        // Trade with berkan
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + johannesAuthToken)
                .when()
                .get("/trade/1")
                .then()
                .statusCode(200);
    }

}
