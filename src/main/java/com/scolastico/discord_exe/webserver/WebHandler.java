package com.scolastico.discord_exe.webserver;

import com.sun.net.httpserver.HttpExchange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface WebHandler {

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface WebHandlerRegistration {
        public String[] context() default "";
    }

    public String onWebServer(HttpExchange httpExchange);

}
