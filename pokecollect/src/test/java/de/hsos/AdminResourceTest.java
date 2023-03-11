package de.hsos;


import de.hsos.shared.dto.PokemonLightDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class AdminResourceTest {


    // Src: https://umberto.xyz/post/2021-01-06-restassured-get-that-token/ and adjusted with insomnia keycloak video
    public String getAuthToken() {
        Response loginResponse = given()
                .auth()
                .preemptive()
                .basic("frontend-ionic", "perm")
                .formParam("username", "berkan")
                .formParam("password", "berkan")
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
    public void testGetAllUser() {
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/admins/user")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetUserById() {

        String getAuthToken = this.getAuthToken();

        given()
                .header("Authorization", "Bearer " + getAuthToken)
                .when()
                .get("/admins/user/1")
                .then()
                .statusCode(200)
                .body("id", CoreMatchers.is(1))
                .body("name", CoreMatchers.is("berkan"))
                .body("coinPurse.balance", CoreMatchers.is(100000));
    }
    @Test
    public void testAddAndDeletePokemonToUserById() {

        String authToken = this.getAuthToken();

        List<PokemonLightDTO> pokemonLightDTOList = new ArrayList<>();
        PokemonLightDTO pokemonLightDTO = new PokemonLightDTO(1L, 1);
        pokemonLightDTOList.add(pokemonLightDTO);
        Jsonb json = JsonbBuilder.create();
        String result = json.toJson(pokemonLightDTOList);


        // Add Pokemon
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .body(result)
                .post("/admins/1")
                .then()
                .statusCode(200)
                .body("id", CoreMatchers.is(1))
                .body("amount", CoreMatchers.is(2)); // amount is 2 because we had 1 pokemon with id 1 before


        // Delete added Pokemon
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .body(result)
                .delete("/admins/1")
                .then()
                .statusCode(200)
                .body("id", CoreMatchers.is(1))
                .body("amount", CoreMatchers.is(1));

    }
}
