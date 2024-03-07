package org.alessio.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.PermissionsAllowed;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.alessio.exception.CustomNotFoundException;
import org.alessio.middleware.PathParamValidator;
import org.alessio.models.Publisher;
import org.alessio.response.CustomResponse;
import org.alessio.security.RequiredScope;
import org.alessio.services.PublisherService;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Path("/api/v1/publishers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublisherController {
    @Inject
    PublisherService publisherService;

    @Inject
    SecurityIdentity identity;

    @GET
    @RequiredScope("publishers_read")
    public Response getAll() {
        List<Publisher> publishers = publisherService.findAll();

        if (publishers.isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Not found",
                    "No publishers were found",
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
                "Publishers found successfully",
                publishers,
                200
        );
        return Response.status(Response.Status.OK).entity(customResponse).build();
    }

    @GET
    @Path("/{id}")
    public Response getPublisherById(@PathParam("id") String pathId) {
        Publisher publisher = validateAndFindPublisherById(pathId);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Publisher found successfully",
                publisher,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    @POST
    public Response createPublisher(Publisher publisher) {
        // If books is present in client payload, return BAD REQUEST
        if (publisher.getBooks() != null && !publisher.getBooks().isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Invalid Operation",
                    "You cannot create a publisher's books directly. You must edit the publisher on the book",
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
                "Publisher created successfully",
                publisherService.create(publisher),
                200
        );
        return Response.status(Response.Status.OK).entity(customResponse).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updatePublisher(@PathParam("id") String pathId, Publisher publisherDetails) {
        Publisher existingPublisher = validateAndFindPublisherById(pathId);

        // If books is present in client payload, return BAD REQUEST
        if (publisherDetails.getBooks() != null && !publisherDetails.getBooks().isEmpty()) {
            CustomResponse customResponse = new CustomResponse(
                    true,
                    "Invalid Operation",
                    "You cannot create a publisher's books directly. You must edit the publisher on the book",
                    null,
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST).entity(customResponse).build();
        }


        // If firstName is present in client payload
        if (publisherDetails.getName() != null) {
            // Remove space before and after firstName, also double-spaces, with trim()
            String trimmedName = publisherDetails.getName().trim().replaceAll("\\s+", " ");
            // Validate firstName, or else add error string
            if (!trimmedName.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")) {
                // Logic here...
                // ...
                CustomResponse customResponse = new CustomResponse(
                        true,
                        "Validation Error",
                        "Name must contain only letters",
                        null,
                        400
                );
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(customResponse)
                        .build();
            } else {
                publisherDetails.setName(trimmedName); // Update client payload name
            }
        }

        Publisher updatedPublisher = publisherService.update(existingPublisher.getId(), publisherDetails);

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Publisher updated successfully",
                updatedPublisher,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePublisher(@PathParam("id") String pathId) {
        Publisher publisherToDelete = validateAndFindPublisherById(pathId);

        publisherService.delete(publisherToDelete.getId());

        CustomResponse customResponse = new CustomResponse(
                false,
                null,
                "Publisher with ID " + publisherToDelete.getId() +" deleted successfully",
                null,
                200
        );
        return Response.status(Response.Status.OK)
                .entity(customResponse)
                .build();
    }

    private Publisher validateAndFindPublisherById(String pathId) {
        Long id = PathParamValidator.validateAndConvert(pathId);
        return publisherService.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Publisher with ID " + id + " not found"));
    }
}