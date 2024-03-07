package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.alessio.exception.CustomNotFoundException;
import org.alessio.response.CustomResponse;
import org.alessio.middleware.PathParamValidator;
import org.alessio.models.Author;
import org.alessio.models.Book;
import org.alessio.repositories.AuthorRepository;
import org.alessio.repositories.PublisherRepository;
import org.alessio.services.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/v1/books")
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
    public Response getAll() {
        List<Book> books = bookService.findAll();

        if (books.isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Not found",
                    "No books were found",
                    null,
                    404
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(customResponse)
                    .build();
        }

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Books found successfully",
                books,
                200
        );
        return Response.status(Response.Status.OK).entity(customResponse).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") String pathId) {
        Book book = validateAndFindBookById(pathId);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Book found successfully",
                book,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    @POST
    public Response createBook(Book book) {
        // Check author errors
        List<CustomResponse> authorErrors = verifyAuthors(book);
        if (!authorErrors.isEmpty()) {
            String errorMessage = authorErrors.stream()
                    .map(CustomResponse::getMessage)
                    .collect(Collectors.joining(", "));
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Multiple Author Errors",
                    errorMessage,
                    null,
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(customResponse)
                    .build();
        }

        // Check publisher errors
        CustomResponse customResponse = verifyPublisher(book);
        if (customResponse != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(customResponse)
                    .build();
        }

        // Book creation
        Book newBook = bookService.create(book);
        if (newBook == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CustomResponse(
                            true,
                            "Internal server Error",
                            "An unexpected error has occurred",
                            null,
                            500
                    ))
                    .build();
        }

        return getBookById(newBook.getId().toString());

        // return Response.status(Response.Status.OK).entity(bookService.create(book)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateBook(@PathParam("id") String pathId, Book bookDetails) {
        Book existingBook = validateAndFindBookById(pathId);

        // Trim title
        if (bookDetails.getTitle() != null) {
            String trimmedTitle = bookDetails.getTitle().trim().replaceAll("\\s+", " ");
            bookDetails.setTitle(trimmedTitle); // Update client payload Title
        }

        // Check authors errors
        List<CustomResponse> authorErrors = verifyAuthors(bookDetails);
        if (!authorErrors.isEmpty()) {
            String errorMessage = authorErrors.stream()
                    .map(CustomResponse::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CustomResponse(
                            true,
                            "Multiple Author Errors",
                            errorMessage,
                            null,
                            400
                    ))
                    .build();
        }

        // Check publisher errors
        CustomResponse publisherErrorResponse = verifyPublisher(bookDetails);
        if (publisherErrorResponse != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(publisherErrorResponse)
                    .build();
        }

        Book updatedBook = bookService.update(existingBook.getId(), bookDetails);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Book updated successfully",
                updatedBook,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();

        // return this.getBookById(updatedBook.getId().toString());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") String pathId) {
        Book bookToDelete = validateAndFindBookById(pathId);

        bookService.delete(bookToDelete);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Book with ID " + bookToDelete.getId() +" deleted successfully",
                null,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }



    private List<CustomResponse> verifyAuthors(Book book) {
        List<CustomResponse> errors = new ArrayList<>();
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            for (Author author : book.getAuthors()) {
                if (author.getId() != null && authorRepository.findById(author.getId()) == null) {
                    errors.add(new CustomResponse("Not Found", "Author with ID " + author.getId() + " not found"));
                }
            }
        }
        return errors;
    }


    private CustomResponse verifyPublisher(Book book) {
        if (book.getPublisher() != null && book.getPublisher().getId() != null) {
            if (publisherRepository.findById(book.getPublisher().getId()) == null) {
                return new CustomResponse(
                        true,
                        "Not Found",
                        "Publisher with ID " + book.getPublisher().getId() + " not found",
                        null,
                        400
                );
            }
        }
        return null;
    }

    private Book validateAndFindBookById(String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);
        return bookService.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Book with ID " + id + " not found"));
    }
}
