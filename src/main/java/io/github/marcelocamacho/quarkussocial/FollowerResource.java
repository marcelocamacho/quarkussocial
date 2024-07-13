package io.github.marcelocamacho.quarkussocial;

import io.github.marcelocamacho.quarkussocial.dto.FollowerRequest;
import io.github.marcelocamacho.quarkussocial.model.Followers;
import io.github.marcelocamacho.quarkussocial.repository.FollowerRepository;
import io.github.marcelocamacho.quarkussocial.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
    
    
}
