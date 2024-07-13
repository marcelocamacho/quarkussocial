package io.github.marcelocamacho.quarkussocial.dto;

import io.github.marcelocamacho.quarkussocial.dto.FollowersResponse;
import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {
    
    private Integer followersCount;
    private List<FollowersResponse> content;

}
