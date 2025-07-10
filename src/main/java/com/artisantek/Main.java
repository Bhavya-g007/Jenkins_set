package com.artisantek;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // This will serve "index.html" from "resources/public" on the root path "/"
            // It's the most efficient way to serve a single-page or static site root.
            config.spaRoot.addFile("/", "/public/index.html", Location.CLASSPATH);
        }).start(5000);

        System.out.println("\nApplication is running on http://localhost:5000");
        System.out.println("Press Ctrl+C to stop.");
    }
} 