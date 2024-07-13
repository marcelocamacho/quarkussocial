package io.github.marcelocamacho.quarkussocial.dto;

import io.github.marcelocamacho.quarkussocial.model.Followers;
import lombok.Data;

@Data
public class FollowersResponse {
    private Long id;
    private String name;

    public FollowersResponse() {
    }

    public FollowersResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public FollowersResponse(Followers follower) {
        this(follower.getId(),follower.getFollower().getName());
    }
}
