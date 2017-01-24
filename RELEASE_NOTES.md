# Release Notes

## 2.0.0-RC1

- major refactoring, pretty much everything has changed
- added RAML 1.0 support, 1.0 is now the target for both halves of the system
- now using JavaPoet for code generation (for documentation and support)
- raml-to-jaxrs: target is now JAX-RS 2.0
- raml-to-jaxrs: configuration is mainly done through RAML 1.0 annotations to the spec
- raml-to-jaxrs: command line support & maven plugin
- raml-to-jaxrs: added specific examples
- raml-to-jaxrs: added a `jaxrs-code-generator` plugin to control responses/requests generation
- jaxrs-to-raml: JAX-RS 2.0

## 2.0.0 (upcoming)

- raml-to-jaxrs: gradle plugins
- raml-to-jaxrs: `jaxrs-code-generator` configuration for RAML 1.0 types
- jaxrs-to-raml: support for @FormParam
