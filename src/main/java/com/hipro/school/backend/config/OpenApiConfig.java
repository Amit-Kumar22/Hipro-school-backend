package com.hipro.school.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * OpenAPI Configuration for Hipro School Management System
 *
 * Production-ready API documentation with JWT Authentication.
 */

@OpenAPIDefinition(
        info = @Info(
                title = "Hipro School Management System API",
                version = "v1.0.0",
                description = """
                        Hipro School Management System is a multi-tenant SaaS platform
                        built using Spring Boot for managing educational institutions
                        efficiently and securely.
                        
                        The platform enables multiple schools to operate independently
                        while being managed from a single scalable application.
                        
                        Available Modules:
                        
                        • Authentication & Authorization (JWT)
                        • School Management
                        • Student Management
                        • Parent Management
                        • Teacher & Staff Management
                        • Class & Section Management
                        • Subject Management
                        • Attendance Management
                        • Fee Management
                        • Examination & Result Management
                        • Timetable Management
                        • Homework & Assignment Management
                        • Library Management
                        • Holiday & Leave Management
                        • Notification & Communication
                        • Media & Document Management
                        • Transport Management
                        • School Settings & Configuration
                        • Reports & Analytics
                        
                        Key Features:
                        
                        • Multi-School (Multi-Tenant) Architecture
                        • Secure JWT Authentication
                        • Role-Based Access Control (RBAC)
                        • RESTful APIs
                        • Modular & Scalable Design
                        • Swagger / OpenAPI Documentation
                        • Production-Level Logging
                        • Global Exception Handling
                        • MySQL / PostgreSQL Support
                        • Redis Cache Support
                        • File Storage Integration
                        
                        This documentation contains complete API specifications,
                        request/response models, authentication details,
                        and endpoint information.
                        """,
                contact = @Contact(
                        name = "Hipro School Development Team",
                        email = "support@hiproschool.in",
                        url = "https://www.hiproschool.in"
                ),
                license = @License(
                        name = "Hipro School Commercial License",
                        url = "https://www.hiproschool.in/license"
                ),
                termsOfService = "https://www.hiproschool.in/terms"
        ),

        servers = {

                @Server(
                        description = "Local Development",
                        url = "http://localhost:8082"
                ),

                @Server(
                        description = "Development Server",
                        url = "http://76.13.245.49:8082"
                ),

                @Server(
                        description = "Production Server",
                        url = "https://api.hiproschool.in"
                )
        },

        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)

@SecurityScheme(
        name = "bearerAuth",
        description = """
                JWT Authentication using Bearer Token.
                
                Authorization Steps:
                
                1. Authenticate using the Login API.
                2. Copy the JWT token returned in the response.
                3. Click the 'Authorize' button in Swagger UI.
                4. Enter the token as:
                
                   Bearer your-jwt-token
                
                5. Click Authorize and access all secured APIs.
                """,
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class OpenApiConfig {

}