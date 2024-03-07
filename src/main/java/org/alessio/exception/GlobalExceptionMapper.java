package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.alessio.response.CustomResponse;

public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Server logging error
        exception.printStackTrace();

        CustomResponse customResponse = new CustomResponse(
                true,
                "Internal Server Error",
                "An unexpected error has occurred",
                null,
                500
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(customResponse)
                .build();
    }
}
