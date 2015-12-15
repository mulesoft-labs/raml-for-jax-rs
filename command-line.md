![](http://raml.org/images/logo.png)

#Command Line Interface (CLI)

##RAML to JAX-RS

###Installation

You can download the command line tool from [here](http://raml-tools.mulesoft.com/raml-for-jax-rs/CLI/raml-to-jax-rs.jar), or you can clone this repository and build it locally by following these steps:

- Go to the `raml-to-jaxrs/core/` folder.
- Run `mvn assembly:assembly -DdescriptorId=jar-with-dependencies`
- Go to the `target` folder, where you will find the generated jar (`raml-JAX-RS-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar`).
- **Optional**: This documentation asumes that the JAR is called: `raml-to-jax-rs.jar`. You might want to rename yours to follow the documentation.

This jar file (downloaded or built yourself) contains all that you need to run the RAML-to-JAX-RS tool from the command line.

### Aquiring staging build
You may use following urls to aquite latest staging builds of command line tools:
 - http://raml-tools-stage.mulesoft.com.s3.amazonaws.com/raml-for-jax-rs/CLI/jax-rs-to-raml.jar (JAXRS to RAML)
 - http://raml-tools-stage.mulesoft.com.s3.amazonaws.com/raml-for-jax-rs/CLI/raml-to-jax-rs.jar (RAML to JAXRS)

### Usage

In order to generate your JAX-RS code, you must run it by invoking

```terminal
java -cp [path-to-the-jar] org.raml.jaxrs.codegen.core.Launcher [options]
```

####Options

 * basePackageName: The package name for generated sources.
 * sourceDirectory: The directory where the RAML definition is located.
 * outputDirectory: The directory where the generated JAVA files will be located.
 * jaxrsVersion: The JAX-RS version that the generated code will be compatible with. It's **optional** and `1.1` by default.
 * useJsr303Annotations: Flag for indicating if JSR 303 should be used. It's **optional**.
 * jsonMapper: Version of the JSON mapper to be used. It's **optional** and `jackson1` by default.
 * mapToVoid: If set this option to true methods with empty bodies will have void resource type, otherwise we still will generate response wrapper for them.

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



###Installation

You can download the command line tool from [here](http://raml-tools.mulesoft.com/raml-for-jax-rs/CLI/jax-rs-to-raml.jar),
or you can clone this repository and build it locally by following these steps:

- Go to the `com.mulesoft.jaxrs.raml.generator` folder.
- Run `mvn clean package install`
- Go to the `com.mulesoft.jaxrs.raml.generator.annotations/target` folder where you will find the generated jar
(`com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar`).
- **Optional:** This documentation asumes that the JAR is called: `jax-rs-to-raml.jar`. You might want to rename yours to follow the documentation.


This jar file (downloaded or built yourself) contains all that you need to run JAX-RS-to-RAML tool from the command line.


###Usage

In order to generate your RAML code, you must run it by invoking


```
javac [classes]
      -sourcepath [path-to-your-java-source-code]
      -classpath [your-classpath]
      -processorpath [path-to-your-jar]/jax-rs-to-raml.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=[path]
      -implicit:class
```

####Options
- classes: The JAVA classes from which you want to generate a RAML representation. **Note:** The classes must represent web-services
(this means, the classes must utilize, at least, one of the following JAVAX annotations: `PUT`, `GET`, `POST`, `OPTIONS`, `Produces` or `Path`).
- sourcepath: The folder containing the packages where the classes (and classes' dependencies) are located.
**Note:** If your Java packages are contained in more then one source folders, you must include all of them. For example: `-sourcepath src1/java/main:src2/java/main`.
Note that path separator symbol is not the same for all OS. MacOS and Linux use ':' while Windows uses ';'.
- classpath: The classpath of the Java project, the *classes* belong to. **Note**: If any path contains a space, the whole value must be passed between quotes. For example: `-classpath "my libs/lib1.jar:lib/lib2.jar"`. **Important**: This jar must be included as a dependency: `jsr311-api-1.1.1.jar`. You can download it [here](https://jsr311.java.net/)
- processorpath: The path to the jar you downloaded (or built).
- processor: Indicates the annotation processor. Use: `com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor`
- -Aramlpath: The folder which the generated RAML definition will be placed.
- -implicit: `class` specifies that the annotations in the dependent files must also be resolved by the processor.

For example:

Consider a project with this structure (or part of it):
```terminal
.
── src
│   └── main
│       └── java
│           └── shop
│               ├── services
│               │   ├── ProductResource.java
│               │   ├── CustomerResource.java
```


Executing:
```
javac src/java/main/services/CustomerResource.java src/java/main/services/ProductResource.java
      -sourcepath src/main/java
      -classpath lib
      -processorpath jax-rs-to-raml/jaxrs.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=output/raml
      -implicit:class
```
will result in your generated RAML placed on the `output/raml` folder.


___

**Consideration**: The current version of the javac plugin is not supporting XML Examples, JSON Schemas, and JSON Examples.
These features are currently working on the Eclipse Plugin version and will be supported in future versions for the javac plugin as well.
