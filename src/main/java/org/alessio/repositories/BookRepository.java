package org.alessio.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.alessio.models.Book;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
}
