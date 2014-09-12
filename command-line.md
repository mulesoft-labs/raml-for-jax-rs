![](http://raml.org/images/logo.png)

#Command Line Interface (CLI)

##RAML to JAX-RS

###Installation

You can download the command line tool from http://raml-tools.mulesoft.com/jaxrs-to-raml/javac/ramlToJAX-RS.jar, or you can clone this repository and build it locally by following these steps:

- Go to the `raml-to-jaxrs/core/` folder.
- Run `mvn assembly:assembly -DdescriptorId=jar-with-dependencies`
- Go to `target` folder, where you will find the generated jar (`raml-JAX-RS-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar`).
- **Optional**: This documentation asumes that the JAR is called: `raml-to-jax-rs.jar`. You might want to rename yours to follow the documentation.

This jar file (downloaded or built yourself) contains all that you need to run the RAML-to-JAX-RS tool from the command line.

### Usage

In order to generate your JAX-RS code, you must run it by invoking

```terminal
java -cp [path-to-the-jar] org.raml.jaxrs.codegen.core.Launcher [options]
```

####Options

 * basePackageName: The package name for generated sources.
 * sourceDirectory: The directory where the RAML definition is located.
 * outputDirectory: The directory where the generated JAVA files will be located.
 * JAX-RSVersion: The JAX-RS version that the generated code will be compatible with. It's **optional** and `1.1` by default.
 * useJsr303Annotations: Flag for indicating if Jsr301 should be used. It's **optional**.
 * jsonMapper: Version of the JSON mapper to be used. It's **optional** and `jackson1` by default.

####Example

```terminal
  java -cp raml-to-jax-rs.jar org.raml.jaxrs.codegen.core.Launcher  
    -basePackageName com.somecompany.sample
    -outputDirectory target/generatedCode
    -sourceDirectory src/resources/raml
```
The command above will process the RAML definition located in `src/resources/raml` and generate the corresponding JAVA code in `target/generatedCode` folder.
The JAVA classes will be in the `com.somecompany.sample` package.

###Java API

Actually API developers should seldom have to use the core generator but instead should use a plug-in for their build tool. But there is an example of how to do it:

####Example:

    File outputDirectory = new File("/some/path/to/target/code-gen");

    Configuration configuration = new Configuration();
    configuration.setOutputDirectory(outputDirectory);
    configuration.setSourceDirectory(inputDirectory)
    configuration.setBasePackageName("org.raml.jaxrs.test");

    InputStreamReader ramlReader = new InputStreamReader(getClass().getResourceAsStream("/org/raml/full-config-with-patch.yaml"));

    new Generator().run(ramlReader, configuration);



##JAX-RS to RAML.

JAX-RS to RAML conversion is implemented as javac plugin, so to run it your need to run javac with our annotation processor

###Installation instructions

You can download the command tool jar from http://raml-tools.mulesoft.com/jaxrs-to-raml/javac/JAX-RS.raml.apt.jar, or you can clone this repository and
build it locally by following these steps:

1. Go to the project root folder.
2. `cd com.mulesoft.jaxrs.raml.generator`
3. `mvn clean package install`
4. `cd ../com.mulesoft.jaxrs.raml.generator.annotations`
5. `cd ../com.mulesoft.jaxrs.raml.generator.annotations/target`
6. Inside this folder, you will find the generated jar (`com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar`).


This jar file (downloaded or built yourself) contains all that you need to run JAX-RS-to-RAML as a javac plugin.


###Usage Guide

```
javac TestResource1.java
      -sourcepath <path-to-your-java-source-code>
      -classpath <your-classpath>
      -processorpath <path-to-your-jar>/JAX-RS.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=<path>
      -implicit:class
```

For example:

```
javac HelloWorldRest.java
      -sourcepath tests
      -classpath jsr311-api-1.1.1.jar
      -processorpath JAX-RS.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=helloworld
      -implicit:class
```

Let's take a look at the command line in more detail:
 * `HelloWorldRest.java`: Your Java file
 * `-sourcepath`: Tells javac where to find Java sources.
 * `-classpath jsr311-api-1.1.1.jar`: Adds jsr311 annotations to the classpath.
 * `-processorpath com.mulesoft.jaxrs.raml.generator.annotations.jar`: Tells javac where to find the annotation processor.
 * `-processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor`: Adds the annotation processor to the javac flow.
 * ``-Aramlpath=helloworld`: Tells javac which is the destination folder for the (to-be)-generated RAML definition.
 * `-implicit:class`: Tells javac that the annotations in the dependent files must also be resolved by the processor.

**Notes:**
- If you built the jar yourself, it will probably have some name like `com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar`.
Remember that you need to pass that filename for the `processorpath` parameter.
- If `ramlpath` ends with `.raml` (for example `ramlfolder/testresource.raml`), it will be treated as the target filename.
Otherwise it will be treated as a directory, and the target filename will be `path/generated.raml` (e.g. `-Aramplpath=myraml` will result in the destination `myraml/generated.raml`).
In both cases the parent directories for a destination file will be automatically created if needed.

___

**Consideration**: The current version of the javac plugin is not supporting XML Examples, JSON Schemas, and JSON Examples.
These features are currently working on the Eclipse Plugin version and will be supported in future versions for the javac plugin as well.
