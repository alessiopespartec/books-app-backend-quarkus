package org.alessio.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.alessio.models.Publisher;

@ApplicationScoped
public class PublisherRepository implements PanacheRepository<Publisher> {
}
