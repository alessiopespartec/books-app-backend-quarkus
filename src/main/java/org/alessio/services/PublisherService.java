package org.alessio.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.alessio.models.Author;
import org.alessio.models.Publisher;
import org.alessio.repositories.PublisherRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PublisherService {
    @Inject
    PublisherRepository publisherRepository;

    public List<Publisher> findAll() {
        return publisherRepository.listAll();
    }

    public Optional<Publisher> findById(Long id) {
        return publisherRepository.findByIdOptional(id);
    }

    @Transactional
    public Publisher create(Publisher publisher) {
        publisherRepository.persist(publisher);
        return publisher;
    }

    @Transactional
    public Publisher update(Long id, Publisher publisherDetails) {
        Optional<Publisher> optionalPublisher = publisherRepository.findByIdOptional(id);

        if(optionalPublisher.isPresent()) {
            Publisher publisher = optionalPublisher.get();

            // Name
            if(publisherDetails.getName() != null) {
                publisher.setName(publisherDetails.getName());
            }
            return publisher;
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        publisherRepository.deleteById(id);
    }
}
