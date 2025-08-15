package com.example.inventory;

// Importing core Spring Boot utilities
import org.springframework.boot.SpringApplication; // Class that starts your Spring Boot app and server
import org.springframework.boot.autoconfigure.SpringBootApplication; // Annotation that does essential auto-configuration

/**
 * This is the MAIN ENTRY POINT for your Spring Boot application.
 * 
 * Why is this needed?
 * - Every Spring Boot app starts from a "main" method, which initializes the Spring framework and all your beans.
 * - @SpringBootApplication is a special annotation that tells Spring:
 *      1. Turn on auto-configuration (finds sensible defaults for common Spring stuff so you don't have to write boilerplate)
 *      2. Performs component scanning (automatically discovers your @Controller, @Service, @Repository, etc. classes)
 *      3. Configures this class as the "source" for starting the application
 * 
 * What should you take care?
 * - Only ONE @SpringBootApplication class needed per project (usually at root package).
 * - Don't put business logic here; keep this file ONLY to launch the app.
 */
@SpringBootApplication // Activates Spring Boot’s auto-configuration and component scanning
public class InventorySystemApplication {

    /**
     * The main() method is the first thing that Java runs when starting your app.
     * 
     * Why use SpringApplication.run?
     * - It bootstraps the Spring context, discovers all your application components (beans), sets up the web server, etc.
     * - Passes any command line arguments to the application if needed
     * 
     * What to take care:
     * - Never put your logic inside main(). Use services, controllers, and other layers instead!
     */
    public static void main(String[] args) {
        SpringApplication.run(InventorySystemApplication.class, args);  // Start and run the Spring Boot application
    }
}
