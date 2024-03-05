package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.alessio.exception.ErrorPayload;
import org.alessio.exception.PathParamValidator;
import org.alessio.models.Author;
import org.alessio.models.Book;
import org.alessio.repositories.AuthorRepository;
import org.alessio.repositories.PublisherRepository;
import org.alessio.services.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookController {
    @Inject
    BookService bookService;
    @Inject
    private AuthorRepository authorRepository;
    @Inject
    private PublisherRepository publisherRepository;

    @GET
    public List<Book> getAll() {
        return bookService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        return bookService.findById(id)
                .map(book -> Response.ok(book).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createBook(Book book) {
        // Check author errors
        List<ErrorPayload> authorErrors = verifyAuthors(book);
        if (!authorErrors.isEmpty()) {
            String errorMessage = authorErrors.stream()
                    .map(ErrorPayload::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorPayload("Multiple Author Errors", errorMessage)).build();
        }

        // Check publisher errors
        ErrorPayload publisherError = verifyPublisher(book);
        if (publisherError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(publisherError).build();
        }

        // Book creation
        return Response.status(Response.Status.OK).entity(bookService.create(book)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateBook(@PathParam("id") String pathId, Book book) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        // Find book, or else throw NOT FOUND payload
        Optional<Book> existingBookOpt = bookService.findById(id);
        if (existingBookOpt.isEmpty()) {
            ErrorPayload errorPayload = new ErrorPayload("Not Found", "Book with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorPayload).build();
        }

        // Trim title
        if (book.getTitle() != null) {
            String trimmedTitle = book.getTitle().trim().replaceAll("\\s+", " ");
            book.setTitle(trimmedTitle); // Update client payload Title
        }

        // Check authors errors
        List<ErrorPayload> authorErrors = verifyAuthors(book);
        if (!authorErrors.isEmpty()) {
            String errorMessage = authorErrors.stream()
                    .map(ErrorPayload::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorPayload("Multiple Author Errors", errorMessage)).build();
        }

        // Check publisher errors
        ErrorPayload publisherError = verifyPublisher(book);
        if (publisherError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(publisherError).build();
        }

        Book updatedBook = bookService.update(id, book);
        return Response.ok(updatedBook).build();
    }

    // DELETE ONE...



    private List<ErrorPayload> verifyAuthors(Book book) {
        List<ErrorPayload> errors = new ArrayList<>();
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            for (Author author : book.getAuthors()) {
                if (author.getId() != null && authorRepository.findById(author.getId()) == null) {
                    errors.add(new ErrorPayload("Not Found", "Author with ID " + author.getId() + " not found"));
                }
            }
        }
        return errors;
    }


    private ErrorPayload verifyPublisher(Book book) {
        if (book.getPublisher() != null && book.getPublisher().getId() != null) {
            if (publisherRepository.findById(book.getPublisher().getId()) == null) {
                return new ErrorPayload("Not Found", "Publisher with ID " + book.getPublisher().getId() + " not found");
            }
        }
        return null;
    }
}
