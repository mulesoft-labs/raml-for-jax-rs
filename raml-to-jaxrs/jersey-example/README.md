![](http://raml.org/images/logo.png)

# RAML to JAX-RS codegen - Jersey Example

This project demonstrates the usage of the RAML-JAXRS Maven plug-in in a Jersey 2 API project.

## Pre-requisites

- JDK6 or better
- Maven 3

## Running and Testing

Run `org.raml.jaxrs.example.Main` for example with:

    mvn exec:java

Then:

    curl -H "Authorization: s3cr3t" http://localhost:8181/presentations?title=Some%20title

    curl -H "Authorization: s3cr3t" -H "Content-Type: application/json" -d '{"title":"New presentation"}' http://localhost:8181/presentations
