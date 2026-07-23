package com.ebock.infrastructure.config;

public class SchemaContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setEnvironment(String environment) {
        CONTEXT.set(environment);
    }

    public static String getEnvironment() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}