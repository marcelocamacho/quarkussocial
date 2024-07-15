package io.github.marcelocamacho.quarkussocial;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.marcelocamacho.quarkussocial.dto.CreateUserRequest;
import io.github.marcelocamacho.quarkussocial.dto.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserResourceTest {
    
    @Test
    @DisplayName("Should create an user successfully")
    public void createUserTest(){
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(22);

        var response = given().contentType(ContentType.JSON).body(user)
            .when().post("/users")
            .then().extract().response();

        assertEquals(201,response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    public void createValidationErrorTest(){
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response = given().contentType(ContentType.JSON).body(user)
            .when().post("/users")
            .then().extract().response();

            assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
            assertEquals("Validation Error", response.jsonPath().getString("message"));

            List<Map<String,String>> errors = response.jsonPath().getList("errors");
            assertNotNull(errors.get(0).get("message"));
            assertNotNull(errors.get(1).get("message"));
            assertEquals("Age field is required.", errors.get(0).get("message"));
    }
}
