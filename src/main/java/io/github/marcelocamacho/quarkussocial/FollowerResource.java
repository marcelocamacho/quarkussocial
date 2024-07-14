package io.github.marcelocamacho.quarkussocial;

import java.util.stream.Collectors;

import io.github.marcelocamacho.quarkussocial.dto.FollowerRequest;
import io.github.marcelocamacho.quarkussocial.dto.FollowersPerUserResponse;
import io.github.marcelocamacho.quarkussocial.dto.FollowersResponse;
import io.github.marcelocamacho.quarkussocial.model.Followers;
import io.github.marcelocamacho.quarkussocial.repository.FollowerRepository;
import io.github.marcelocamacho.quarkussocial.repository.UserRepository;
import io.vertx.core.http.GoAway;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private UserRepository userRepository;
    private FollowerRepository followRepository;
    
    public FollowerResource(UserRepository userRepository, FollowerRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){
        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        var follower = userRepository.findById(request.getFollowerId());
        if(follower == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(userId.equals(request.getFollowerId())){
            return Response.status(Response.Status.CONFLICT)
                .entity("You can't follow youserlf").build();
        }

        boolean isFollower = followRepository.follows(follower,user);

        if(!isFollower){
            var entity = new Followers();
            entity.setUser(user);
            entity.setFollower(follower);
            followRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){

        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = followRepository.findByUser(userId);
        FollowersPerUserResponse response = new FollowersPerUserResponse();
        response.setFollowersCount(list.size());
        
        var followerList = list.stream().map( FollowersResponse::new ).collect(Collectors.toList());

        response.setContent(followerList);

        return Response.ok(response).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser( @PathParam("userId") Long userId, @QueryParam("followerId") Long followId){
        var user = userRepository.findById(userId);
        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followRepository.deleteByFollowerAndUser(followId,userId);

        return Response.noContent().build();
    }
}
