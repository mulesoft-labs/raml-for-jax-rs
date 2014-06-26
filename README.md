# JAXRS-to-RAML

##Introduction

The goal of JAXRS-to-RAML is to provide a full featured and easy to use tool to generate
a RAML API definition from an existing JAXRS-annotated Java code.

It's based on analyzing the existing code annotations.
(See "Supported annotations" to understand which annotations are being handled in the current version).


## Project Modules
This far, you can use JAXRS-to-RAML the following ways:
- Eclipse/Mule Studio plugin: Perform the RAML generation from your IDE by selecting the package containing the source Java classes.
- Javac plugin: Perform the RMAL generation from Command Line.
- Runtime: Use JAXRS-to-RAML in your Java application to generate a RAML definition from some Java Code in Runtime.

## Key supported features:
- All basic action types, path annotations, path, query, and form parameters.
- Generating a resource tree based on the jersey resources available in the source path.
- Inferring media types when possible.
- Including javadoc documentation to resources, methods, and parameters descriptions.
- Statically determinable sub-resources (no overriding).
- Default values and validations for parameters (using javax.validation annotations).
- [XML/Json schemas and examples(stubs) generation with JAXB](https://github.com/mulesoft/jaxrs-to-raml/blob/master/jaxb.md)

### Supported Annotations
####JaxRS annotations:
These annotations have exactly the same semantical meaning than in JAXRS:
- Path
- Consumes, Produces
- QueryParam, FormParam, PathParam, HeaderParam
- DELETE, GET, HEAD, OPTIONS, POST, PUT
- DefaultValue

**Note:** CookieParam and MatrixParam annotations are not supported in this version. A deep discussion
about how these should be represented in RAML must be held in order to have a good implementation. Feel free to contribute
with ideas/opinions about it.

####Swagger Annnotations:
When these annotations exist, the tool is able to determine the possible response codes and generate the proper documentation.
- ApiResponse
- ApiResponses

####Validation annotations:
These annotations are translated as RAML parameters contrains.
- NotNull
- Pattern
- Min
- DecimalMin
- Max
- DecimalMax

## Installation and usage guides
- [Installation instructions/Usage guide for Eclipse or Mule Studio](https://github.com/mulesoft/jaxrs-to-raml/blob/master/eclipseplugin.md)
- [Installation instructions/Usage guide for using as plugin to javac](https://github.com/mulesoft/jaxrs-to-raml/blob/master/javac.md)

## Future features
- Injection of request parameters to fields
