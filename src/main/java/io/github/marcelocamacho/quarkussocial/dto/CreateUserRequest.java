package io.github.marcelocamacho.quarkussocial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Name field is required")
    private String name;
    
    @NotNull(message = "Age field is required.")
    private Integer age;

}
