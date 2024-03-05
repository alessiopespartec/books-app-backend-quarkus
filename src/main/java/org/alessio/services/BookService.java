package org.alessio.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alessio.models.Author;
import org.alessio.models.Book;
import org.alessio.repositories.BookRepository;

import java.util.*;

@ApplicationScoped
public class BookService {
    @Inject
    BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.listAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findByIdOptional(id);
    }

    @Transactional
    public Book create(Book book) {
        bookRepository.persist(book);
        return book;
    }

    @Transactional
    public Book update(Long id, Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findByIdOptional(id);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            // Title
            if (bookDetails.getTitle() != null) {
                book.setTitle(bookDetails.getTitle());
            }
            // Year
            if (bookDetails.getYear() != null) {
                book.setYear(bookDetails.getYear());
            }
            // AUTHORS
            // Identify authors to remove:
            // add to an array any authors that are in the database book but not in the client payload
            List<Author> authorsToRemove = new ArrayList<>();
            for (Author currentAuthor : book.getAuthors()) {
                if (!bookDetails.getAuthors().contains(currentAuthor)) {
                    authorsToRemove.add(currentAuthor);
                }
            }
            // Remove mismatched authors (two-way)
            for (Author authorToRemove : authorsToRemove) {
                book.removeAuthor(authorToRemove); // <-- this method is two-way
            }
            // Add new authors from the payload that don't already exist in database book
            List<Author> existingAuthors = new ArrayList<>(book.getAuthors());
            for (Author newAuthor : bookDetails.getAuthors()) {
                if (!existingAuthors.contains(newAuthor)) {
                    book.addAuthor(newAuthor); // <-- this method is two-way
                }
            }
            // PUBLISHER
            // Check if the publisher needs to be updated
            if (bookDetails.getPublisher() != null && !bookDetails.getPublisher().equals(book.getPublisher())) {
                // If database book publisher exists, remove it
                if (book.getPublisher() != null) {
                    book.getPublisher().removeBook(book); // <-- this method is two-way
                }
                // Set this book in client payload publisher
                bookDetails.getPublisher().addBook(book); // <-- this method is two-way
            }



            return book;
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
