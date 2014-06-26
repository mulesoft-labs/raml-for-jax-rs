## Installation instructions
 
 Download command tool jar using the following url: http://jaxrstoraml.appspot.com/jaxrs.raml.apt.jar
 This jar file contains all that you need to run jaxrs to raml as plugin to javac.
 
 
## Usage guide


javac TestResource1.java -classpath (your classpath) -processorpath (path to your jar)/jaxrs.raml.apt.jar

-processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor 
-Aramlpath=(path)

If (path) ends with .raml (smth like ramlfolder/testresource.raml), it would be treated as target filename. Else it would be treated as directory, and target filename would be (path)/generated.raml (e.g. -Aramplpath=myraml would result in dest file myraml/generated.raml). In both cases parent directories for destination file would be created automatically, if needed


