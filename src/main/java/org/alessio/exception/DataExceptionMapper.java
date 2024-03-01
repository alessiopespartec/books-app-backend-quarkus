package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.exception.DataException;

@Provider
public class DataExceptionMapper implements ExceptionMapper<DataException> {

    @Override
    public Response toResponse(DataException exception) {
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError("Validation data error");
        errorPayload.setMessage("One or more fields exceed the maximum length allowed.");

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorPayload)
                .build();
    }
}
