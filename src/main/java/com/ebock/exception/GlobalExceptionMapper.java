package com.ebock.exception;

import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import org.apache.ibatis.exceptions.PersistenceException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.sql.SQLException;
import java.util.Map;

import static io.quarkus.arc.ComponentsProvider.LOG;

@ApplicationScoped
public class GlobalExceptionMapper {

    private String getSafeMessage(Exception exception, String defaultMessage) {
        return (exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : defaultMessage;
    }

    @ServerExceptionMapper(IllegalArgumentException.class)
    public Response handleIllegalArgument(IllegalArgumentException exception) {
        String safeMessage = getSafeMessage(exception, "Invalid argument provided.");
        LOG.warnf("Bad request: %s", safeMessage);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                        "error", "Bad Request",
                        "message", safeMessage
                )).build();
    }

    @ServerExceptionMapper(UnsupportedOperationException.class)
    public Response handleUnsupportedOperation(UnsupportedOperationException exception) {
        String safeMessage = getSafeMessage(exception, "Operation not supported.");
        LOG.warnf("Unsupported operation: %s", safeMessage);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                        "error", "Bad Request",
                        "message", safeMessage
                )).build();
    }

    @ServerExceptionMapper(UnauthorizedException.class)
    public Response handleUnauthorizedException(UnauthorizedException exception) {
        String safeMessage = getSafeMessage(exception, "Access unauthorized. Token missing or invalid.");
        LOG.warnf("Unauthorized operation: %s", safeMessage);

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of(
                        "error", "Unauthorized",
                        "message", safeMessage
                )).build();
    }

    @ServerExceptionMapper(ForbiddenException.class)
    public Response handleForbiddenException(ForbiddenException exception) {
        String safeMessage = getSafeMessage(exception, "Access forbidden. Insufficient privileges.");
        LOG.warnf("Forbidden operation: %s", safeMessage);

        return Response.status(Response.Status.FORBIDDEN)
                .entity(Map.of(
                        "error", "Forbidden",
                        "message", safeMessage
                )).build();
    }

    @ServerExceptionMapper(PersistenceException.class)
    public Response handleMyBatisPersistenceError(PersistenceException exception) {

        // Find cause of the exception
        Throwable cause = exception.getCause();
        while (cause != null && !(cause instanceof SQLException)) {
            cause = cause.getCause();
        }

        LOG.errorf(exception, "Caught exception: %s", exception.getMessage());

        if (cause instanceof SQLException sqlException) {
            String sqlState = sqlException.getSQLState();

            // Log the SQL error
            LOG.errorf("MyBatis Database Error Code caught. SQLState: %s", sqlState);

            // "23505" = Unique/Duplicate Key Violations
            if ("23505".equals(sqlState)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(Map.of(
                                "error", "Conflict",
                                "message", "There is a conflict error."
                        ))
                        .build();
            }
        }

        // Other database error
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                        "error", "Internal Server Error",
                        "message", "A database error occurred processing your request."
                ))
                .build();
    }
}