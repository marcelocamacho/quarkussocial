package io.github.marcelocamacho.quarkussocial;

import java.util.stream.Collectors;

import io.github.marcelocamacho.quarkussocial.dto.CreatePostRequest;
import io.github.marcelocamacho.quarkussocial.dto.PostResponse;
import io.github.marcelocamacho.quarkussocial.model.Post;
import io.github.marcelocamacho.quarkussocial.model.User;
import io.github.marcelocamacho.quarkussocial.repository.FollowerRepository;
import io.github.marcelocamacho.quarkussocial.repository.PostRepository;
import io.github.marcelocamacho.quarkussocial.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    
    private UserRepository userRepository;
    private PostRepository postRepository;
    FollowerRepository followerRepository;

    public PostResource(UserRepository userRepository,PostRepository postRepository, FollowerRepository followerRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost( @PathParam("userId") Long userId, CreatePostRequest request ){

        User user = userRepository.findById(userId);


        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);
        
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId){

        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Your forgot header followeId").build();
        }

        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(followerId);
        boolean isFollower = followerRepository.follows(follower, user);

        if(!isFollower && !followerId.equals(userId)){
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts.").build();
        }

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime",Sort.Direction.Descending) ,user);

        // a palavra var também pode ser usado ao invés de tipar pois a partir do java 11 ele faz inferência de tipo
        var list = query.list(); 

        var postResponseList = list.stream()
            //As duas formas de executar o map são equivalentes nesse caso
            //.map(post -> PostResponse.fromEntity(post))
            .map(PostResponse::fromEntity) //MethodReference
            .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
}
