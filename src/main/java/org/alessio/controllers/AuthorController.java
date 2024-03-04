package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alessio.exception.ErrorPayload;
import org.alessio.models.Author;
import org.alessio.services.AuthorService;
import jakarta.ws.rs.core.Response;

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
    public Response getAuthorById(@PathParam("id") Long id) {
        return authorService.findById(id)
                .map(author -> Response.ok(author).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Author createAuthor(Author author) {
        return authorService.create(author);
    }

    @PATCH
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") Long id, Author author) {
        // Find author, or else throw NOT FOUND payload
        Optional<Author> existingAuthorOpt = authorService.findById(id);
        if (existingAuthorOpt.isEmpty()) {
            ErrorPayload errorPayload = new ErrorPayload("Not Found", "Author with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorPayload).build();
        }

        // Create error array of strings
        List<String> errors = new ArrayList<>();

        // If firstName is present in client payload
        if (author.getFirstName() != null) {
            // Remove space before and after firstName, also double-spaces, with trim()
            String trimmedFirstName = author.getFirstName().trim().replaceAll("\\s+", " ");
            // Validate firstName, or else add error string
            if (!trimmedFirstName.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
                errors.add("firstName must contain only letters");
            } else {
                author.setFirstName(trimmedFirstName); // Update client payload firstName
            }
        }

        // Same for lastName
        if (author.getLastName() != null && !author.getLastName().matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
            errors.add("lastName must contain only letters");
        }

        // If error array is not empty, return BAD REQUEST payload
        if (!errors.isEmpty()) {
            String errorMessage = String.join(", ", errors);
            ErrorPayload errorPayload = new ErrorPayload("Validation Error", errorMessage);
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
    public Response deleteAuthor(@PathParam("id") Long id) {
        authorService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
