## javac plugin installation instructions

Download the command tool jar from http://jaxrstoraml.appspot.com/jaxrs.raml.apt.jar.

This jar file contains all that you need to run JAXRS-to-RAML as a javac plugin.


## Usage guide

```
javac TestResource1.java
      -classpath <your-classpath>
      -processorpath <path-to-your-jar>/jaxrs.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=<path>
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
 * `-implicit:class`: Tells javac that the annotations in dependent files must also be resolved by the processor.



If `ramlpath` ends with `.raml` (for example `ramlfolder/testresource.raml`), it will be treated as the target filename.
Otherwise it will be treated as a directory, and the target filename will be `path/generated.raml` (e.g. `-Aramplpath=myraml` will result in the destination `myraml/generated.raml`).
In both cases the parent directories for a destination file will be automatically created if needed.
