package com.ebock.infrastructure.database;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import com.ebock.infrastructure.config.SchemaContextHolder;
import java.sql.Connection;
import java.sql.Statement;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SchemaRoutingInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // Get schema
        String targetSchema = SchemaContextHolder.getEnvironment();

        if (targetSchema == null || targetSchema.trim().isEmpty()) {
            targetSchema = "ebock";
        }

        // Get the connection
        Connection connection = (Connection) invocation.getArgs()[0];

        // Change the search_path
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO " + targetSchema);
        }

        return invocation.proceed();
    }
}
