package io.github.marcelocamacho.quarkussocial;

import static io.restassured.RestAssured.given;

import javax.swing.text.AbstractDocument.Content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.marcelocamacho.quarkussocial.dto.CreatePostRequest;
import io.github.marcelocamacho.quarkussocial.dto.CreateUserRequest;
import io.github.marcelocamacho.quarkussocial.model.User;
import io.github.marcelocamacho.quarkussocial.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setAge(30);
        user.setName("Test");

        userRepository.persist(user);
        this.userId = user.getId();

    }

    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");
        given().contentType(ContentType.JSON)
        .body(postRequest)
        .pathParam("userId", this.userId)
        .when().post().then().statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserID = 999;

        given().contentType(ContentType.JSON)
        .body(postRequest)
        .pathParam("userId", inexistentUserID)
        .when().post().then().statusCode(404);
    }
    
}
