![](http://raml.org/images/logo.png)

#Command Line Interface (CLI)

##RAML to JAX-RS

###Installation instructions.

You can download the command tool jar from http://raml-tools.mulesoft.com/jaxrs-to-raml/javac/ramlToJAX-RS.jar, or you can clone this repository and build it locally by following these steps:

Go to the project root folder.
cd /raml-to-jaxrs/core/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
cd target
Inside this folder, you will find the generated jar (raml-JAX-RS-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar).
This jar file (downloaded or built yourself) contains all that you need to run RAML-to-JAX-RS as a command line


### Usage

To do it you should type following commandline

    java -cp raml-jaxrs-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar  org.raml.jaxrs.codegen.core.Launcher

and add some configuration  options to it:

 * basePackageName - package name for generated sources
 * sourceDirectory - directory to look for raml files
 * outputDirectory - directory to store generated java files
 * JAX-RSVersion default 1.1
 * useJsr303Annotations should we use Jsr301 or not
 * jsonMapper version of json mapper to use defaults to 'jackson1'

So your sample command line might looks like: 
 
`java -cp raml-jaxrs-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar  org.raml.jaxrs.codegen.core.Launcher  -basePackageName com.somecompany.sample -outputDirectory destination -sourceDirectory .`

###Using Java API to generate JAX-RS on the fly:

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
