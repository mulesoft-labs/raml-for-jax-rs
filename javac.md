## Installation instructions
 
 Download command tool jar using the following url: http://jaxrstoraml.appspot.com/jaxrs.raml.apt.jar
 This jar file contains all that you need to run jaxrs to raml as plugin to javac.
 
 
## Usage guide


javac TestResource1.java -classpath (your classpath) -processorpath (path to your jar)/jaxrs.raml.apt.jar

-processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor 
-Aramlpath=(path)

For example in simple case your command like may look like: 

javac HelloWorldRest.java -sourcepath tests -classpath jsr311-api-1.1.1.jar -processorpath com.mulesoft.jaxr
s.raml.generator.annotations.jar -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor -Aramlpath=helloworld -implicit:class 

Let's look on this command line in details: 
 * HelloWorldRest.java - java file 
 * -sourcepath this options tells javac where to find java sources
 * -classpath jsr311-api-1.1.1.jar adds jsr311 annotations to class path
 * -processorpath com.mulesoft.jaxrs.raml.generator.annotations.jar tells javac where to find annotation processor
 * -processor com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor adds annotation processor to javac flow
 * -Aramlpath=helloworld tells desctination folder for RAML
 * -implicit:class tells javac that annotations in dependent files should also be resolvable to processo.r.



If (path) ends with .raml (smth like ramlfolder/testresource.raml), it would be treated as target filename. Else it would be treated as directory, and target filename would be (path)/generated.raml (e.g. -Aramplpath=myraml would result in dest file myraml/generated.raml). In both cases parent directories for destination file would be created automatically, if needed


