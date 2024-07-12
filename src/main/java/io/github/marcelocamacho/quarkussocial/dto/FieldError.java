package io.github.marcelocamacho.quarkussocial.dto;

import lombok.Getter;
import lombok.Setter;

public class FieldError {
    @Getter
    @Setter
    private String field;
    
    @Getter
    @Setter
    private String message;


    
    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

}
