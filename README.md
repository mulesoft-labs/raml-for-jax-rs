jaxrs-to-raml
=============

Starting from JAX-RS-annotated Java code, generate a RAML API description
- Description

Key supported features:
 * All basic action types, path annotations, path,query, and form parameters
 * Generating resource tree basing on jersey resources available on the source path
 * Inferring media types when possible
 * Including javadoc documentation to descriptions of resources, methods, parameters.
 * Statically determinable sub resources. (no overriding)
 * Default values for parameters


Thanks in advance for your feedback,
Dmitry

- Installation instructions
 
  The repo contains eclipe update site project. It allows to install eclipse-plugin version using default eclipse
  functionality. 
  
  APT processor class name - com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor

- Usage Example

- Future features
  * Injection of request parameters to fields
  * Constraint annotations to raml constraints conversion
  * XML/Json Schema generation with JAXB
