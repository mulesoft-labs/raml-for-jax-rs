![](http://raml.org/images/logo.png)
RAML for JAXRS
===============

#Introduction

The goal of RAML for JAXRS is to provide a set of tools to work with these technologies in a way of being able to scaffold a JAVA + JAXRS application based on an existing RAML API definition (Code Generation),
or its roundtrip, generate the RAML API definition based on an existing JAVA + JAXRS application (Documentation).

## Consideration
RAML for JAXRS is the result of merging two projects that started individually and at different times. The idea of joining these, pursuits the purpose of providing a better experience to the developers when installing and using it.

#Project Modules

- [Eclipse / Anypoint Studio plugin: Perform RAML->JAXRS and JAXRS to RAML generation embedded in your IDE. Installation instructions/User guide.](eclipseplugin.md)
- [Maven Plugin: Perform RAML->JAXRS and JAXRS to RAML generation embedded in your IDE. Installation instructions/User guide.](/raml-to-jaxrs/maven-plugin/README.md)
- [Javac plugin: Perform JAXRS->RAML from the command line. Installation instructions/User guide.](/jaxrs-to-raml/javac.md)
- [Jar: Perform RAML->JAXRS from the command line. Installation instructions/User guide.](/raml-to-jaxrs/core/README.md)

#Design principles

# RAML Generation (from JAXRS)
All distributions (Eclipse plugin, Maven Plugin, and Javac Plugin) works in the following way.

- Accepting all basic action types, path annotations, path, query, and form parameters.
- Generating a resource tree based on the Jersey resources available in the source path.
- Inferring media types when possible.
- Including Javadoc documentation to resources, methods, and parameters descriptions.
- Statically determinable sub-resources (no overriding).
- Default values and validations for parameters (using javax.validation annotations).
- [XML/Json schemas and examples(stubs) generation with JAXB](/jaxrs-to-raml/jaxb.md)

##Currently Supported

###JAXRS Annotations:
In JAXRS-to-RAML, these annotations have exactly the same semantical meaning as in JAXRS:
- Path.
- Consumes, Produces.
- QueryParam, FormParam, PathParam, HeaderParam.
- DELETE, GET, HEAD, OPTIONS, POST, PUT.
- DefaultValue.

**Note:** CookieParam and MatrixParam annotations are not supported in this version. A deep discussion
about how these should be represented in RAML must be held in order to have a good implementation. Feel free to [contribute
with ideas/opinions about it](https://github.com/mulesoft/jaxrs-to-raml/issues?labels=Cookie%26Matrix+params&milestone=&page=1&state=closed).
###Extended Annotations
The following annotations are not part of JAXRS specification itself. However, these are useful to describe RESTful APIs when working with
JAVA projects, and so, it was decided to add support for them.

####Swagger Annotations:
If the project is using these swagger annotations, the tool is able to determine the possible response codes
and generate the proper documentation.
- ApiResponse
- ApiResponses

####Validation Annotations:
These annotations are interpreted as RAML parameters constrains.
- NotNull.
- Pattern.
- Min.
- DecimalMin.
- Max.
- DecimalMax.

## Not yet supported

- Examples and JSON schema generation in javac compiler  plugin mode.
- Extracting possible reponse codes by analizing java source code
- Traits and resource type suggestions


# JAXRS Generation (from RAML)
All distributions (Eclipse Plugin, Maven Plugin, and Jar) works in the following ways:

- Interfaces are generated and will be regenerated when the RAML definition changes.
- One interface is generated per top level resource, sub-resources are defined as different methods in the same interface.
- A response object wrapper is created for each resource action in order to guide the implementer in producing only results
that are compatible with the RAML definition.
- Custom annotations are generated for HTTP methods that are not part of the core JAX-RS specification.
- Objects are generated based on json schemas to represent request/response entities.
- English is the language used in the interface and method names generation.

##Currently Supported
- JAX-RS 1.1 and 2.0,
- JSR-303 annotations, except `@Pattern` because RAML uses ECMA 262/Perl 5 patterns and javax.validation uses Java ones,
and with `@Min`/`@Max` support limited to non decimal minimum/maximum constraints defined in RAML.
- Model object generation based on JSON schemas, with Jackson 1, 2 or Gson annotations.

###Supported annotations 

Path.
Consumes, Produces.
QueryParam, FormParam, PathParam, HeaderParam.
DELETE, GET, HEAD, OPTIONS, POST, PUT.
DefaultValue.
NotNull.
Min.
DecimalMin.
Max.
DecimalMax.

## Not yet supported

- Generation of JAXB annotated classes based on XML Schemas


#Future Features

- **For RAML->JAXRS:**
 - Documentation needs a lot of improvements, completion and examples. Most developers will be able to figure the missing gaps by themselves and looking at the Jersey example, but we will enhance documentation soon.
 - There is no hosting of Maven artifacts yet, so users need to download from git and install in the local repo for now.
 - Maven Archetypes implementation, for the most common project types that this plug-in could be used with, like: plain JAR if this is used as part of another project, plain WAR if this is to be deployed on a JavaEE container, WAR+specific JAX-RS implementation (for example Jersey) if to be deployed on a web container that does not provide JAX-RS, standalone JAR with a particular JAX-RS implementation.
 - Project assume developers are familiar with JAX-RS to get started. There is only one example in Jersey included, but we may want to add examples in other implementations of JAX-RS to help developers choose between the different options.
- **For JAXRS->RAML**
 - Injection of request parameters to fields.
 - Support for XML examples, JSON examples, and JSON schemas for the Javac plugin.

#Examples

- [JAXRS->RAML examples] (/jaxrs-to-raml/examples/README.md)  
- [RAML->JAXRS examples] (/raml-to-jaxrs/jersey-example/README.md)

### Contributing
If you are interested in contributing some code to this project, thanks! Please submit a [Contributors Agreement](https://api-notebook.anypoint.mulesoft.com/notebooks#bc1cf75a0284268407e4) acknowledging that you are transferring ownership.

To discuss this project, please use its github issues or the [RAML forum](http://forums.raml.org/).
