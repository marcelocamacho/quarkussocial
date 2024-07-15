package io.github.marcelocamacho.quarkussocial;


import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.marcelocamacho.quarkussocial.dto.FollowerRequest;
import io.github.marcelocamacho.quarkussocial.model.User;
import io.github.marcelocamacho.quarkussocial.repository.FollowerRepository;
import io.github.marcelocamacho.quarkussocial.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
public class FollowerResourceTest {
    
    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp(){
        var user = new User();
        user.setAge(20);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(20);
        follower.setName("Fulano");
        userRepository.persist(follower);
        followerId = follower.getId();
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(ContentType.JSON).body(body)
            .pathParam("userId", userId)
            .when().put()
            .then().statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(Matchers.is("You can't follow youserlf"));
    }

    @Test
    @DisplayName("Should return 404 when userId doen't exist")
    public void userNotFoundTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 99;

        given().contentType(ContentType.JSON).body(body).pathParam("userId",inexistentUserId)
            .when().put().then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    public void followUserTest(){
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given().contentType(ContentType.JSON).body(body)
            .pathParam("userId", userId)
            .when().put().then().statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
