## RAML Generation from JAX-RS
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
- @HeaderParam annotated parameters
- Javadoc description of methods (when the sources have been provided)
- Default values for parameters

NOTE: Not all sorts of types are supported. The current implementation only
supports Java primitive types.

### Using the maven plugin
The [jax-rs-test-resources](/jaxrs-test-resources/pom.xml) contains an example usage of how to
setup the plugin.

This plugin should be run minimally after the compile goal, since it needs the compiled classes of
the project for RAML generation.

The example provided above shows all the possible configurations. None of them are mandatory however.
The [jax-rs-test-resources](/jaxrs-test-resources/pom.xml) pom file shows what the default values are.

For convenience, we list the configurations here:
- input - The input is the directory where to fetch the target classes.
  it defaults to "${project.build.outputDirectory}"
- outputDirectory - The output directory is where the generated RAML file will
  be located. It defaults to "${project.build.sourceDirectory}"
- outputFileName - The output file name is how the resulting RAML file will be named.
  This defaults to "${project.artifactId}.raml"
- sourceDirectory - The source directory corresponds to where the corresponding source
  classes can be found. This is used to extract user provided documentation. It
  defaults to "${project.build.directory}"

### Using the CLI
The CLI is very simplistic. It provides little less features because less emphasis was put on its development.
The project [jax-to-raml-cli](/jaxrs-to-raml-cli/pom.xml) contains the cli artifact.
The first argument provided on the CLI is the directory where to find the compiled classes and
the second one is the path to the generated RAML file.
