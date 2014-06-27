## javac Plugin Installation Instructions

You can download the command tool jar from http://jaxrstoraml.appspot.com/jaxrs.raml.apt.jar, or you can clone this repository and
build it locally by following these steps:
1. Go to the project root folder.
2. `cd com.mulesoft.jaxrs.raml.generator`
3. `mvn clean package install`
4. `cd ../com.mulesoft.jaxrs.raml.generator.annotations`
5. `cd ../com.mulesoft.jaxrs.raml.generator.annotations/target`


This jar file contains all that you need to run JAXRS-to-RAML as a javac plugin.


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

Let's look on this command line in details:
 * `HelloWorldRest.java`: Your java file
 * `-sourcepath`: Tells javac where to find java sources.
 * `-classpath jsr311-api-1.1.1.jar`: Adds jsr311 annotations to the classpath.
 * `-processorpath com.mulesoft.jaxrs.raml.generator.annotations.jar`: Tells javac where to find the annotation processor.
 * `-processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor`: Adds the annotation processor to the javac flow.
 * ``-Aramlpath=helloworld`: Tells javac which is the destination folder for the (to-be)-generated RAML definition.
 * `-implicit:class`: Tells javac that the annotations in the dependent files must also be resolved by the processor.

**Notes:**
- If you built the jar yourself, it will probably has some name like `com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar`.
Remember that you need to pass that filename for the `processorpath` parameter
- If `ramlpath` ends with `.raml` (for example `ramlfolder/testresource.raml`), it will be treated as the target filename.
Otherwise it will be treated as a directory, and the target filename will be `path/generated.raml` (e.g. `-Aramplpath=myraml` will result in the destination `myraml/generated.raml`).
In both cases the parent directories for a destination file will be automatically created if needed.

___

**Consideration**: The current version of the javac plugin is not supporting XML Examples, JSON Schemas, and JSON Examples.
These features are currently working on the Eclipse Plugin version and will be supported in future versions for the javac plugin as well.
