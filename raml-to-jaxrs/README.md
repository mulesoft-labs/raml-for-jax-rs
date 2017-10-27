## JAX-RS Generation from RAML (raml-to-jaxrs)
All distributions works in the following ways:

- Interfaces are generated and will be regenerated when the RAML definition changes;
- One interface is generated per top level resource, sub-resources are built from the subresources defined in the RAML parser;
- A response object wrapper is created for each resource action in order to guide the implementer in producing only results
that are compatible with the RAML definition;
- Custom annotations are generated for HTTP methods that are not part of the core JAX-RS specification;
- Objects defined as RAML 1.0 objects are generated.  These objects are not as flexible as we want right now, but we
are hoping to make them more flexible are we figure this out;
- Objects are also generated based on JSON schemas to represent request/response entities;
- English is the language used in the interface and method names generation.

### Currently Supported
- JAX-RS 2.0
- JSR-303 annotations, except `@Pattern` because RAML uses ECMA 262/Perl 5 patterns and `javax.validation` uses Java ones;
and with `@Min`/`@Max` support limited to non decimal minimum/maximum constraints defined in RAML;
- Model object generation based on JSON schemas, with Jackson 1, 2 or Gson annotations;
- Generation of JAXB-annotated classes based on XML Schemas;
- Annotations can be generated using one or a combination of three formats: [Gson](GSON.md), [Jackson](JACKSON.md) and [JAXB](JAXB.md)
- [Generation plugins](jaxrs-code-generator/README.md): the generation of the interfaces and the response classes can be controlled using a set of RAML annotations in the source RAML file.

### Using the Maven plugin
There are several examples of projects using the maven plugin.
The configuration of these projects is documented [here](examples/maven-examples/README.md).

- [simple-json-example](examples/maven-examples/simple-json-example/): a simple example of JSON schema generation
- [simple-xml-example](examples/maven-examples/simple-xml-example/): a simple example of XML schema generation
- [simple-raml08](examples/maven-examples/simple-raml08/): a simple example using a RAML 0.8 definition
- [scalar-types](examples/maven-examples/scalar-types/): an example using [RAML 1.0 Scalar Types](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md#scalar-types)
- [raml-defined-example](examples/maven-examples/raml-defined-example/): an elaborate example using [RAML 1.0 Type Expressions](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md#type-expressions) and [generation plugins](examples/maven-examples/features/README.md)
- [jaxb-example](examples/maven-examples/jaxb-example/): an example showing JAXB-annotated class generation based on XML Schemas
- [features](examples/maven-examples/features/): an example showing how the [generation plugins](examples/maven-examples/features/README.md) feature works, includes [several use cases](examples/maven-examples/features/USE_CASES.md)

### Using the Gradle plugin
The documentation of the Gradle plugin can be found [here](raml-to-jaxrs-gradle-plugin/README.md).
There are also some examples [here](examples/gradle-examples/).

### Using the CLI
The project [raml-to-jaxrs-cli](raml-to-jaxrs-cli/) contains the CLI artifact. It is setup to build a JAR with dependencies which can then be used in the command line.

```
usage: ramltojaxrs -d <arg> [-g <arg>] [-m <arg>] [-r <arg>] [-s <arg>]
 -d,--directory <arg>             generation directory
 -j,--json-mapper <arg>           jsonschema2pojo annotation types
                                  (jackson, jackson2 or gson)
 -g,--generate-types-with <arg>   generate types with plugins
                                  (jackson, gson, jaxb, javadoc, jsr303)
 -m,--model-package <arg>         model package
 -r,--resource-package <arg>      resource package
 -s,--support-package <arg>       support package
```

E.g.
```
$ cd raml-to-jaxrs/raml-to-jaxrs-cli/
$ mvn clean install
$ java -jar ./target/raml-to-jaxrs-cli-<version>-jar-with-dependencies.jar -d /tmp -r foo.bar ../examples/maven-examples/raml-defined-example/src/main/resources/types_user_defined.raml
```
This will generate the source of a `foo.bar` package inside the folder `/tmp` using the RAML file included in the `raml-defined-example` example.
