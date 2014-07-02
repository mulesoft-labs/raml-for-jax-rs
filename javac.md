## javac Plugin Installation Instructions

You can download the command tool jar from http://raml-tools.mulesoft.com/jaxrs-to-raml/javac/jaxrs.raml.apt.jar, or you can clone this repository and
build it locally by following these steps:

1. Go to the project root folder.
2. `cd com.mulesoft.jaxrs.raml.generator`
3. `mvn clean package install`
4. `cd ../com.mulesoft.jaxrs.raml.generator.annotations`
5. `cd ../com.mulesoft.jaxrs.raml.generator.annotations/target`
6. Inside this folder, you will find the generated jar (`com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar`).


This jar file (downloaded or built yourself) contains all that you need to run JAXRS-to-RAML as a javac plugin.


## Usage Guide

```
javac TestResource1.java
      -sourcepath <path-to-your-java-source-code>
      -classpath <your-classpath>
      -processorpath <path-to-your-jar>/jaxrs.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=<path>
      -implicit:class
```

For example:

```
javac HelloWorldRest.java
      -sourcepath tests
      -classpath jsr311-api-1.1.1.jar
      -processorpath com.mulesoft.jaxrs.raml.generator.annotations.jar
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
