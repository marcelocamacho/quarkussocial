package io.github.marcelocamacho.quarkussocial.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "post_text")
    private String text;

    @Column
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //O método que contiver a annotation PrePersist será chamado sempre antes que o repository.persist() for invocado.
    @PrePersist
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }
}
