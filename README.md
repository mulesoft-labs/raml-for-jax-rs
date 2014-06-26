# jaxrs-to-raml
=============

JAX - RS to RAML project goal is to provide full featured easy to use components for generation of RAML apis description starting from from JAX-RS-annotated Java code

JAX - RS to RAML is based on analizing annotations content.  at the moment following annotations are supported:[Supported annotations](https://github.com/mulesoft/jaxrs-to-raml/blob/master/annotations.md)

## Project content
Currently project contains following modules:

- Eclipse/Mulestudio plugin
- Plugin for Javac
- API to generate RAML api description at runtime

## Key supported features:
 * All basic action types, path annotations, path,query, and form parameters
 * Generating resource tree basing on jersey resources available on the source path
 * Inferring media types when possible
 * Including javadoc documentation to descriptions of resources, methods, parameters.
 * Statically determinable sub resources. (no overriding)
 * Default values for parameters as well as specifying constraints on parameters using javax.validation annotations
 * [XML/Json schemas and examples(stubs) generation with JAXB] (https://github.com/mulesoft/jaxrs-to-raml/blob/master/jaxb.md)


## Installation and usage guides

- [Installation instructions/Usage guide for Eclipse or Mulestudio](https://github.com/mulesoft/jaxrs-to-raml/blob/master/install.md)
- [Installation instructions/Usage guide for using as plugin to javac](https://github.com/mulesoft/jaxrs-to-raml/blob/master/javac.md)


## Future features
  * Injection of request parameters to fields
  * Constraint annotations to raml constraints conversion

