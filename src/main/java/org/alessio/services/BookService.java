package org.alessio.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alessio.models.Author;
import org.alessio.models.Book;
import org.alessio.models.Publisher;
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
        if (book.getPublisher() != null && book.getPublisher().getId() != null) {

            Publisher publisher = book.getPublisher();
            publisher.addBook(book); // <-- this method is two-way
        }

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
            /*
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
            */

            // Check if authors are explicitly provided in the payload
            if (bookDetails.getAuthors() != null) {
                // Clear current authors and set new ones from the payload
                book.getAuthors().clear();
                book.getAuthors().addAll(bookDetails.getAuthors());
                // Since it's a ManyToMany relationship, you don't need to manually update the authors side
            }
            /*

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
            */

            // Check if publisher is explicitly provided in the payload
            if (bookDetails.getPublisher() != null) {
                // If different from the current publisher, update it
                if (!bookDetails.getPublisher().equals(book.getPublisher())) {
                    if (book.getPublisher() != null) {
                        book.getPublisher().removeBook(book); // Detach the book from the old publisher
                    }
                    book.setPublisher(bookDetails.getPublisher()); // Attach the book to the new publisher
                    bookDetails.getPublisher().addBook(book); // Ensure the book is added to the new publisher's book list
                }
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
