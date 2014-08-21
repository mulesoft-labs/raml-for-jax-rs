![](http://raml.org/images/logo.png)

# RAML JAX-RS Codegen

## Objective

The goal of this project is to support a RAML-first approach for JAX-RS enabled API projects.

It consists in a set of tools that API developers can use to generate JAX-RS annotated interfaces
and supporting classes out of one or several RAML files.
It is then the responsibility of the developer to implement these interfaces in concrete classes that reify the API logic.

## Design principles

- Interfaces are generated and will be regenerated when the RAML definition changes,
- One interface is generated per top level resource, sub-resources are defined as different methods in the same interface.
- A response object wrapper is created for each resource action in order to guide the implementer in producing only results
that are compatible with the RAML definition,
- Custom annotations are generated for HTTP methods that are not part of the core JAX-RS specification,
- Objects are generated based on schemas to represent request/response entities,
- English is the language used in the interface and method names generation.

## Status

### Currently supported

- JAX-RS 1.1 and 2.0,
- JSR-303 annotations, except `@Pattern` because RAML uses ECMA 262/Perl 5 patterns and javax.validation uses Java ones,
and with `@Min`/`@Max` support limited to non decimal minimum/maximum constraints defined in RAML.
- Model object generation based on JSON schemas, with Jackson 1, 2 or Gson annotations.

### Not yet supported

- Generation of JAXB annotated classes based on XML Schemas

## TO-DO's 

- Documentation needs a lot of improvements, completion and examples. Most developers will be able to figure the missing gaps by themselves and looking at the Jersey example, but we will enhance documentation soon.
- There is no hosting of Maven artifacts yet, so users need to download from git and install in the local repo for now.
- Maven Archetypes implementation, for the most common project types that this plug-in could be used with, like: plain JAR if this is used as part of another project, plain WAR if this is to be deployed on a JavaEE container, WAR+specific JAX-RS implementation (for example Jersey) if to be deployed on a web container that does not provide JAX-RS, standalone JAR with a particular JAX-RS implementation.
- Project assume developers are familiar with JAX-RS to get started. There is only one example in Jersey included, but we may want to add examples in other implementations of JAX-RS to help developers choose between the different options.



## Usage

- [Using the Maven Plug-in](maven-plugin/README.md)
- [Using the Eclipse Plug-in](eclipse-plugin/README.md)
- [Using the Core Generator](core/README.md)

## Examples

- [Jersey 2](jersey-example/README.md)

### Contributing
If you are interested in contributing some code to this project, thanks! Please submit a [Contributors Agreement](https://api-notebook.anypoint.mulesoft.com/notebooks#bc1cf75a0284268407e4) acknowledging that you are transferring ownership.

To discuss this project, please use its github issues or the [RAML forum](http://forums.raml.org/).
