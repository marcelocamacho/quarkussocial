package io.github.marcelocamacho.quarkussocial.repository;

import java.util.List;
import java.util.Optional;

import io.github.marcelocamacho.quarkussocial.model.Followers;
import io.github.marcelocamacho.quarkussocial.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Followers>{

    public boolean follows(User follower,User user){
        var params = Parameters.with("follower", follower).and("user", user).map();
        PanacheQuery<Followers> query = find("follower = :follower and user = :user", params);

        Optional<Followers> result = query.firstResultOptional();

        
        return result.isPresent();
    }

    public List<Followers> findByUser(Long userId){
        PanacheQuery<Followers> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followId, Long userId) {
        var params = Parameters.with("userId", userId).and("followerId", followId).map();

        delete("follower.id =:followerId and user.id =: userId", params);
    }
    
}
