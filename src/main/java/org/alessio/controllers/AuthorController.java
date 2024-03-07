package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.alessio.exception.CustomNotFoundException;
import org.alessio.models.Publisher;
import org.alessio.response.CustomResponse;
import org.alessio.middleware.PathParamValidator;
import org.alessio.models.Author;
import org.alessio.services.AuthorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/api/v1/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorController {
    @Inject
    AuthorService authorService;

    @GET
    public Response getAll() {
        List<Author> authors = authorService.findAll();

        if (authors.isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Not found",
                    "No authors were found",
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
                "Authors found successfully",
                authors,
                200
        );
        return Response.status(Response.Status.OK).entity(customResponse).build();
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(@PathParam("id") String pathId) {
        Author author = validateAndFindAuthorById(pathId);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Author found successfully",
                author,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    @POST
    public Response createAuthor(Author author) {
        // If books is present in client payload, return BAD REQUEST payload
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Invalid Operation",
                    "You cannot create an author's books directly. You must edit authors on the book",
                    null,
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(customResponse)
                    .build();
        }

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Author created successfully",
                authorService.create(author),
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") String pathId, Author authorDetails) {
        Author existingAuthor = validateAndFindAuthorById(pathId);

        // If books is present in client payload, return BAD REQUEST payload
        if (authorDetails.getBooks() != null && !authorDetails.getBooks().isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Invalid Operation",
                    "You cannot edit an author's books directly. You must edit authors on each book.",
                    null,
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(customResponse)
                    .build();
        }

        // Create error array of strings
        List<String> validationErrors = new ArrayList<>();

        // If firstName is present in client payload
        if (authorDetails.getFirstName() != null) {
            // Remove space before and after firstName, also double-spaces, with trim()
            String trimmedFirstName = authorDetails.getFirstName().trim().replaceAll("\\s+", " ");
            // Validate firstName, or else add error string
            if (!trimmedFirstName.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
                validationErrors.add("firstName must contain only letters");
            } else {
                authorDetails.setFirstName(trimmedFirstName); // Update client payload firstName
            }
        }

        // Same for lastName
        if (authorDetails.getLastName() != null && !authorDetails.getLastName().matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
            validationErrors.add("lastName must contain only letters");
        }

        // If error array contains at least a value, return it with BAD REQUEST payload
        if (!validationErrors.isEmpty()) {
            String errorMessage = String.join(", ", validationErrors);
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Validation Error",
                    errorMessage,
                    null,
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(customResponse)
                    .build();
        }

        Author updatedAuthor = authorService.update(existingAuthor.getId(), authorDetails);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Author updated successfully",
                updatedAuthor,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") String pathId) {
        Author authorToDelete = validateAndFindAuthorById(pathId);

        authorService.delete(authorToDelete.getId());

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Author with ID " + authorToDelete.getId() +" deleted successfully",
                null,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    private Author validateAndFindAuthorById(String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);
        return authorService.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Author with ID " + id + " not found"));
    }
}
