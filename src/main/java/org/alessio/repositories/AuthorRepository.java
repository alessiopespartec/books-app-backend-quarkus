package org.alessio.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.alessio.models.Author;

@ApplicationScoped
public class AuthorRepository implements PanacheRepository<Author> {
}
