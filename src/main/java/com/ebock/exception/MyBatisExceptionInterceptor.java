package com.ebock.exception;

import jakarta.ws.rs.core.Response;
import org.apache.ibatis.exceptions.PersistenceException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.sql.SQLException;
import java.util.Map;

public class MyBatisExceptionInterceptor {

    @ServerExceptionMapper(PersistenceException.class)
    public Response handleMyBatisPersistenceError(PersistenceException exception) {

        // Find cause of the exception
        Throwable cause = exception.getCause();
        while (cause != null && !(cause instanceof SQLException)) {
            cause = cause.getCause();
        }

        if (cause instanceof SQLException sqlException) {
            String sqlState = sqlException.getSQLState();

            // Log the SQL error
            System.err.println("MyBatis Database Error Code caught. SQLState: " + sqlState);

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