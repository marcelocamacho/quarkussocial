package io.github.marcelocamacho.quarkussocial;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.marcelocamacho.quarkussocial.dto.CreatePostRequest;
import io.github.marcelocamacho.quarkussocial.model.Followers;
import io.github.marcelocamacho.quarkussocial.model.User;
import io.github.marcelocamacho.quarkussocial.repository.FollowerRepository;
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

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setAge(30);
        user.setName("Test");

        var userNotFollower = new User();
        userNotFollower.setAge(30);
        userNotFollower.setName("Test");

        var userFollower = new User();
        userFollower.setAge(30);
        userFollower.setName("Follower");

        userRepository.persist(user);
        userRepository.persist(userNotFollower);
        userRepository.persist(userFollower);

        this.userId = user.getId();
        this.userNotFollowerId = userNotFollower.getId();
        this.userFollowerId = userFollower.getId();

        Followers follower = new Followers();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);


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

    @Test
    @DisplayName("Should to return 404 when user dosn't exist")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 999;

        given().pathParam("userId", inexistentUserId)
            .when().get().then().statusCode(404);
    }

    @Test
    @DisplayName("Should to return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){

        given()
            .pathParam("userId", userId)
            .when().get().then().statusCode(400)
            .body(Matchers.is("Your forgot header followeId"));
    }

    @Test
    @DisplayName("Should to return 404 when follower dosn't exist")
    public void listPostFollowerNotFoundTest(){
        var inexistentFollowerId = 999;

        given()
        .pathParam("userId", userId)
        .header("followerId", inexistentFollowerId)
        .when().get().then().statusCode(400);
    }

    @Test
    @DisplayName("Should return 403 when followerId isn't a follower")
    public void listPostNotAFollower(){
        given()
            .pathParam("userId", userId)
            .header("followerId", userNotFollowerId)
            .when().get()
            .then().statusCode(403)
            .body(Matchers.is("You can't see these posts."));
    }

    @Test
    @DisplayName("Should return posts")
    public void listPostTest(){
        given()
            .pathParam("userId", userId)
            .header("followerId", userFollowerId)
            .when().get()
            .then().statusCode(200);
    }
    
}
