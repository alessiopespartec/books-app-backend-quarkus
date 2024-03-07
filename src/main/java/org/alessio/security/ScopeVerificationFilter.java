package org.alessio.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alessio.response.CustomResponse;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class ScopeVerificationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(RequiredScope.class)) {
            String requiredScope = method.getAnnotation(RequiredScope.class).value();

            // Get JWT token from Authorization Header
            String authorizationHeader = requestContext.getHeaderString("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                .entity(new CustomResponse(
                                        true,
                                        "UNAUTHORIZED",
                                        "Authorization header is missing or invalid",
                                        null,
                                        401
                                ))
                        .build());
                return;
            }
            String token = authorizationHeader.substring("Bearer ".length());
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // Decode JWT to get JSON payload
                String[] chunks = token.split("\\.");
                String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
                JsonNode payloadJson = objectMapper.readTree(payload);
                // Verify if it contains the scope
                String scopes = payloadJson.has("scope") ? payloadJson.get("scope").asText("") : "";
                List<String> scopeList = Arrays.asList(scopes.split(" "));
                if (!scopeList.contains(requiredScope)) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity(new CustomResponse(
                                    true,
                                    "Unauthorized",
                                    "Your user does not have the right scope to access the endpoint",
                                    null,
                                    401
                            ))
                            .build());
                }
            } catch (Exception e) {
                requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
            }
        }
    }
}
