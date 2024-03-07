package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.alessio.response.CustomResponse;

@Provider
public class CustomNotFoundExceptionMapper implements ExceptionMapper<CustomNotFoundException> {
    @Override
    public Response toResponse(CustomNotFoundException exception) {
        CustomResponse customResponse = new CustomResponse(
                true,
                "Not Found",
                exception.getMessage(),
                null,
                404
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(customResponse)
                .build();
    }
}
