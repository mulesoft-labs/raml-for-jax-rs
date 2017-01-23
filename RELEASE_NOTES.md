=======
![](http://raml.org/images/logo.png)
RAML for JAX-RS
===============
Pretty much everything has changed.
- added RAML 1.0 support
- major refactoring, as 1.0 is now the target for both halves of the system.
- now using JavaPoet for code generation.  (for documentation and support).
- raml-to-jaxrs: target is now JAX-RS 2.0.  
- raml-to-jaxrs: configuration is mainly done through RAML 1.0 annotations to the spec.
- raml-to-jaxrs: command line support & maven plugin.  Upcoming gradle plugin.
- raml-to-jaxrs: specific examples [in](raml-to-jaxrs/examples).
- raml-to-jaxrs: upcoming plugin configuration for RAML 1.0 types.
- jaxrs-to-raml: JAX-RS 2.0, see the [README](jaxrs-to-raml/README.md) file
