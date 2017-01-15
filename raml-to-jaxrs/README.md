=======
![](http://raml.org/images/logo.png)
RAML for JAX-RS
===============


## JAX-RS Generation (from RAML)
All distributions (Eclipse Plugin, Maven Plugin, and Jar) works in the following ways:

- Interfaces are generated and will be regenerated when the RAML definition changes.
- One interface is generated per top level resource, sub-resources are built from the subresources defined in the RAML parser. 
- A response object wrapper is created for each resource action in order to guide the implementer in producing only results
that are compatible with the RAML definition.
- Custom annotations are generated for HTTP methods that are not part of the core JAX-RS specification.
- Objects are generated when taken from RAML 1.0 object types.  These objects are not as flexible as we want right now, but we 
are hoping to make them more flexible are we figure this out.
- Objects are also generated based on json schemas to represent request/response entities.
- English is the language used in the interface and method names generation.

###Currently Supported
- JAX-RS 2.0,
- JSR-303 annotations, except `@Pattern` because RAML uses ECMA 262/Perl 5 patterns and javax.validation uses Java ones,
and with `@Min`/`@Max` support limited to non decimal minimum/maximum constraints defined in RAML.
- Model object generation based on JSON schemas, with Jackson 1, 2 or Gson annotations.
- Generation of JAXB annotated classes based on XML Schemas
