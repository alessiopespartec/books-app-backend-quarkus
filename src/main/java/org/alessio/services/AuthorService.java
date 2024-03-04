package org.alessio.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.alessio.models.Author;
import org.alessio.models.Book;
import org.alessio.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthorService {
    @Inject
    AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.listAll();
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findByIdOptional(id);
    }

    @Transactional
    public Author create(Author author) {
        authorRepository.persist(author);
        return author;
    }

    @Transactional
    public Author update(Long id, Author authorDetails) {
        Optional<Author> optionalAuthor = authorRepository.findByIdOptional(id);

        if(optionalAuthor.isPresent()) {
            Author author = optionalAuthor.get();

            // First Name
            if(authorDetails.getFirstName() != null) {
                author.setFirstName(authorDetails.getFirstName());
            }
            // Last Name
            if(authorDetails.getLastName() != null) {
                author.setLastName(authorDetails.getLastName());
            }
            // Date of Birth
            if(authorDetails.getDob() != null) {
                author.setDob(authorDetails.getDob());
            }
            // Nationality
            if(authorDetails.getNationality() != null) {
                author.setNationality(authorDetails.getNationality());
            }
            // Biography
            if(authorDetails.getBiography() != null) {
                author.setBiography(authorDetails.getBiography());
            }
            // Image URL
            if(authorDetails.getImageUrl() != null) {
                author.setImageUrl(authorDetails.getImageUrl());
            }
            /*
            if(authorDetails.getBooks() != null) {
                author.getBooks().clear();
                author.setBooks(authorDetails.getBooks());

                // Each new book will have a new Author
                for (Book book : authorDetails.getBooks()) {
                    book.getAuthors().add(author);
                }
            }
            */
            return author;
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        authorRepository.deleteById(id);
    }
}
