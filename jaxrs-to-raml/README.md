## RAML Generation from JAX-RS (jaxrs-to-raml)
This module provides two main artifacts: a command line tool jar and a maven plugin.
Both of which have the purpose to convert an existing set of compiled JAX-RS Java classes
into their corresponding RAML model.

The provided artifacts reuse the reference JAX-RS implementation code, Jersey, to parse
and extract compiled JAX-RS relevant classes.

### Supported elements of JAX-RS
- @Path annotated classes and methods
- Http verbs annotations: @HEAD, @POST, @GET, @DELETE, @PUT, @OPTIONS
- @Consumes and @Produces media types
- @QueryParam annotated parameters
- @PathParam annotated parameters
- @FormDataParam for multipart files
- @HeaderParam annotated parameters
- Javadoc description of methods (when the sources have been provided)
- Default values for parameters

### Using the Maven plugin
The [`jaxrs-test-resources` package](jaxrs-test-resources/) contains an example usage of how to
setup the plugin, its [pom file](jaxrs-test-resources/pom.xml) shows what the default values are.
There are also several examples [here](jaxrs-to-raml-examples/jaxrs-to-raml-maven-examples/).

This plugin should be run after the `compile` goal, since it needs the compiled classes of
the project for RAML generation.

Here are the configuration options:
- `input` - The input is the directory where to fetch the target classes.
  it defaults to `${project.build.outputDirectory}`
- `outputDirectory` - The output directory is where the generated RAML file will
  be located. It defaults to `${project.build.sourceDirectory}`
- `outputFileName` - The output file name is how the resulting RAML file will be named.
  This defaults to `${project.artifactId}.raml`
- `sourceDirectory` - The source directory corresponds to where the corresponding source
  classes can be found. This is used to extract user provided documentation. It
  defaults to `${project.build.directory}`

### Using the Gradle plugin
The documentation of the Gradle plugin can be found [here](jaxrs-to-raml-gradle-plugin-wrapper/README.md).
There are also some examples [here](jaxrs-to-raml-examples/jaxrs-to-raml-gradle-examples/).

### Using the CLI
The project [`jaxrs-to-raml-cli`](jaxrs-to-raml-cli/) contains the CLI artifact. It is setup to build a JAR with dependencies which can then be used in the command line.

```
usage: jaxrstoraml -a <arg> -o <arg> [-s <arg>] [-t <arg>]
 -a,--applicationDirectory <arg>    application path
 -o,--output <arg>                  RAML output file
 -s,--sourceRoot <arg>              JaxRs source root
 -t,--translatedAnnotations <arg>   translated annotation list (comma
                                    separated
```

E.g.
```
$ cd jaxrs-to-raml/jaxrs-to-raml-cli/
$ mvn clean install
$ java -jar ./target/jaxrs-to-raml-cli-<version>-jar-with-dependencies.jar -a ../jaxrs-test-resources/target/classes -o /tmp/test.raml
```
This will generate the RAML file `/tmp/test.raml` from the compiled classes located at
`../jaxrs-test-resources/target/classes`.

### Turning Java types into RAML types

The way that Java types are turned into RAML types are discussed [here](raml-generator-api/README.md)
