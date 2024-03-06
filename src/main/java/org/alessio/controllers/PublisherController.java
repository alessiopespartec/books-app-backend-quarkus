package org.alessio.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.alessio.exception.ErrorPayload;
import org.alessio.exception.PathParamValidator;
import org.alessio.models.Author;
import org.alessio.models.Publisher;
import org.alessio.services.PublisherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/publishers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublisherController {
    @Inject
    PublisherService publisherService;

    @GET
    public List<Publisher> getAll() {
        return publisherService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getPublisherById(@PathParam("id") String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        return publisherService.findById(id)
                .map(publisher -> Response.ok(publisher).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createPublisher(Publisher publisher) {
        // If books is present in client payload, return BAD REQUEST payload
        if (publisher.getBooks() != null && !publisher.getBooks().isEmpty()) {
            ErrorPayload errorPayload = new ErrorPayload("Invalid Operation", "You cannot create a publisher's books directly. You must edit the publisher on the book.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build();
        }

        return Response.status(Response.Status.OK).entity(publisherService.create(publisher)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updatePublisher(@PathParam("id") String pathId, Publisher publisher) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        // Find publisher, or else throw NOT FOUND payload
        Optional<Publisher> existingPublisherOpt = publisherService.findById(id);
        if (existingPublisherOpt.isEmpty()) {
            ErrorPayload errorPayload = new ErrorPayload("Not Found", "Publisher with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorPayload).build();
        }

        // If books is present in client payload, return BAD REQUEST payload
        if (publisher.getBooks() != null && !publisher.getBooks().isEmpty()) {
            ErrorPayload errorPayload = new ErrorPayload("Invalid Operation", "You cannot create a publisher's books directly. You must edit the publisher on the book.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build();
        }


        // If firstName is present in client payload
        if (publisher.getName() != null) {
            // Remove space before and after firstName, also double-spaces, with trim()
            String trimmedName = publisher.getName().trim().replaceAll("\\s+", " ");
            // Validate firstName, or else add error string
            if (!trimmedName.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
                // Logic here...
                // ...
                ErrorPayload errorPayload = new ErrorPayload("Validation Error", "Name must contain only letters");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build();
            } else {
                publisher.setName(trimmedName); // Update client payload name
            }
        }

        Publisher updatedPublisher = publisherService.update(id, publisher);
        if (updatedPublisher == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(updatedPublisher)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePublisher(@PathParam("id") String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);

        // Find publisher, or else throw NOT FOUND payload
        Optional<Publisher> existingPublisherOpt = publisherService.findById(id);
        if (existingPublisherOpt.isEmpty()) {
            ErrorPayload errorPayload = new ErrorPayload("Not Found", "Publisher with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorPayload).build();
        }

        publisherService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}