## Installation instructions
 
 Download command tool jar using the following url: http://jaxrstoraml.appspot.com/jaxrs.raml.apt.jar
 This jar file contains all that you need to run jaxrs to raml as plugin to javac.
 
 
## Usage guide


javac TestResource1.java -classpath (your classpath) -processorpath (path to your jar)/jaxrs.raml.apt.jar

-processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor 
-Aramlpath=(path to store raml files)

