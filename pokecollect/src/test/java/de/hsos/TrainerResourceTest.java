package de.hsos;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TrainerResourceTest {

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
    public void testUserData() {
        String authToken = this.getAuthToken();
        // Before booster buy
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/whoami")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.is("berkan"))
                .body("coinAmount", CoreMatchers.is(100000));

        // Buy booster
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/buyBooster")
                .then()
                .statusCode(200);


        // After booster buy
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/whoami")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.is("berkan"))
                .body("coinAmount", CoreMatchers.is(99900));

    }

    @Test
    public void testGetAllPokemon(){
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetPokemonById(){
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id", CoreMatchers.is(1))
                .body("id", CoreMatchers.is(1));
    }

    @Test
    public void testGetFavoritePokemon(){
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/favorite")
                .then()
                .statusCode(200);
    }

    @Test
    public void testSetFavoritePokemon(){
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .put("/users/favorite/3")
                .then()
                .statusCode(200)
                .body("pokemonId", CoreMatchers.is(3))
                .body("url", CoreMatchers.is("http://localhost:8081/pokemon/3"));
    }

    @Test
    public void testRemoveFromFavorites(){
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/users/favorite/2")
                .then()
                .statusCode(200)
                .body("pokemonId", CoreMatchers.is(2))
                .body("url", CoreMatchers.is("http://localhost:8081/pokemon/2"));
    }

    @Test
    public void testObtainFreeBooster(){
        String authToken = this.getAuthToken();
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/freeBooster")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateNewUser(){

        given()
                .when()
                .post("/users/register?username=johnny&password=sins")
                .then()
                .statusCode(200)
                .body("id", CoreMatchers.is(3))
                .body("name", CoreMatchers.is("johnny"))
                .body("coinPurse.balance", CoreMatchers.is(500));
    }


}
