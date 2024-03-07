package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.alessio.response.CustomResponse;
import org.alessio.middleware.PathParamValidator;
import org.alessio.models.Author;
import org.alessio.services.AuthorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorController {
    @Inject
    AuthorService authorService;

    @GET
    public List<Author> getAll() {
        return authorService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(@PathParam("id") String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        return authorService.findById(id)
                .map(author -> Response.ok(author).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createAuthor(Author author) {
        // If books is present in client payload, return BAD REQUEST payload
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            CustomResponse errorPayload = new CustomResponse("Invalid Operation", "You cannot create an author's books directly. You must edit authors on the book.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build();
        }

        return Response.status(Response.Status.OK).entity(authorService.create(author)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") String pathId, Author author) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        // Find author, or else throw NOT FOUND payload
        Optional<Author> existingAuthorOpt = authorService.findById(id);
        if (existingAuthorOpt.isEmpty()) {
            CustomResponse errorPayload = new CustomResponse("Not Found", "Author with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorPayload).build();
        }

        // If books is present in client payload, return BAD REQUEST payload
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            CustomResponse errorPayload = new CustomResponse("Invalid Operation", "You cannot edit an author's books directly. You must edit authors on each book.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build();
        }

        // Create error array of strings
        List<String> validationErrors = new ArrayList<>();

        // If firstName is present in client payload
        if (author.getFirstName() != null) {
            // Remove space before and after firstName, also double-spaces, with trim()
            String trimmedFirstName = author.getFirstName().trim().replaceAll("\\s+", " ");
            // Validate firstName, or else add error string
            if (!trimmedFirstName.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
                validationErrors.add("firstName must contain only letters");
            } else {
                author.setFirstName(trimmedFirstName); // Update client payload firstName
            }
        }

        // Same for lastName
        if (author.getLastName() != null && !author.getLastName().matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
            validationErrors.add("lastName must contain only letters");
        }

        // If error array contains at least a value, return it with BAD REQUEST payload
        if (!validationErrors.isEmpty()) {
            String errorMessage = String.join(", ", validationErrors);
            CustomResponse errorPayload = new CustomResponse("Validation Error", errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build();
        }

        Author updatedAuthor = authorService.update(id, author);
        if(updatedAuthor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updatedAuthor).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        // Find author, or else throw NOT FOUND payload
        Optional<Author> existingAuthorOpt = authorService.findById(id);
        if (existingAuthorOpt.isEmpty()) {
            CustomResponse errorPayload = new CustomResponse("Not Found", "Author with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorPayload).build();
        }

        authorService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
