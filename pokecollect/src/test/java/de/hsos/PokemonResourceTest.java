package de.hsos;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class PokemonResourceTest {

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
    public void testGetAllPokemon() {
        given()
                .when()
                .get("/pokemon")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllPokemonWithPaging() {
        given()
                .when()
                .get("/pokemon/page/0")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetPokemonById() {
        given()
                .when()
                .get("/pokemon/1")
                .then()
                .statusCode(200)
                .body("id", CoreMatchers.is(1))
                .body("imageUri", CoreMatchers.is("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/1.svg"))
                .body("name", CoreMatchers.is("bulbasaur"))
                .body("pokemonType.primaryType", CoreMatchers.is("grass"))
                .body("pokemonType.secondaryType", CoreMatchers.is("poison"));

    }


}
