package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alessio.models.Author;
import org.alessio.services.AuthorService;
import jakarta.ws.rs.core.Response;

import java.util.List;

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
        Author updatedAuthor = authorService.update(id, author);
        if(updatedAuthor == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); // Author with that ID not found
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
