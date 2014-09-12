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
javac <classes>
      -sourcepath <path-to-your-java-source-code>
      -classpath <your-classpath>
      -processorpath <path-to-your-jar>/jaxrs.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=<path>
      -implicit:class
```

For example, assume that

* You want to process two web service Java classes which belong to the ```shop.services``` Java package located in the ```src/main/java``` subfolder:
```
src
  main
    java
      shop
        services
          ProductResource.java
          CustomerResource.java
      
```
* These two depend only on those classes which belong to packages located in the the same ```src/main/java``` subfolder.
* Classpath of Java project these classes belong to consists of jar files located in the ```lib``` subfolder.
* The ```jaxrs.raml.apt.jar``` file is located in the ```jax-rs-to-raml``` subfolder.
* You want to generate RAML files into the ```output/raml``` subfolder.

Then your command line request is as follows:

```
javac src/java/main/services/CustomerResource.java src/java/main/services/ProductResource.java
      -sourcepath src/java/main
      -classpath lib
      -processorpath jax-rs-to-raml/jaxrs.raml.apt.jar
      -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor
      -Aramlpath=output/raml
      -implicit:class
```

**Notes:**
- If your Java packages are contained in more then one source folders, you must include all of them:```-sourcepath src1/java/main:src2/java/main```. Note that the path separator symbol is not the same for all operation systems. Mac OS and Linux use ```':'``` while Windows uses ```';'```.
- If a path of your web service Java file contains a space, it must be taken into quotes: ```-javac "/path to my project/Service.java" /src/main/java/services/CustomerResource.java ...```
- If a value of some option contains a path with space, the whole value must be taken into quotes: ```-classpath "my libs/lib1.jar:lib/lib2.jar"```
- A classpath option value can contain jars, class files and folders (which themselves contain jars and class files). Here is example of a complex classpath: ```-classpath folders/lib:/jars/myJar.jar:classes/myClass1.class```
- In all cases above you may use absolute paths instead of relative ones.
- The ```-implicit:class``` option tells javac that the annotations in the dependent files must also be resolved by the processor.
- Your input Java file must utilize one of the following JAVAX annotations: PUT, GET, POST, OPTIONS, Produces and Path. Otherwise, the plugin will not be activated.
- If you have built the jar yourself, it probably has some name like `com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar`.
Remember that you must pass the exact filename for the `processorpath` parameter.
- If `ramlpath` ends with `.raml` (for example `ramlfolder/testresource.raml`), it is treated as the target filename.
Otherwise it is treated as a directory, and the target filename is `generated.raml` (e.g. `-Aramplpath=myraml` will result in the destination `myraml/generated.raml`). In both cases the parent directories for a destination file will be automatically created if needed.

___

**Consideration**: The current version of the javac plugin do not support XML Examples, JSON Schemas, and JSON Examples.
Eclipse Plugin is currently capable of these features, and they will be supported in future versions of the javac plugin as well.
