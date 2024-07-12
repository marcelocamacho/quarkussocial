package io.github.marcelocamacho.quarkussocial.repository;

import io.github.marcelocamacho.quarkussocial.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class PostRepository implements PanacheRepository< Post > {
    
}
