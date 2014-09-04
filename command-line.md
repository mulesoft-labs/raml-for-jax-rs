![](http://raml.org/images/logo.png)

#Perform RAML->JAX-RS from the command line. 

##Installation instructions.

You can download the command tool jar from http://raml-tools.mulesoft.com/JAX-RS-to-raml/javac/ramlToJAX-RS.jar, or you can clone this repository and build it locally by following these steps:

Go to the project root folder.
cd /raml-to-JAX-RS/core/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
cd target
Inside this folder, you will find the generated jar (raml-JAX-RS-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar).
This jar file (downloaded or built yourself) contains all that you need to run RAML-to-JAX-RS as a command line


## Usage

To do it you should type following commandline 

    java -cp raml-JAX-RS-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar  org.raml.jaxrs.codegen.core.Launcher

and add some configuration  options to it:

 * basePackageName - package name for generated sources 
 * sourceDirectory - directory to look for raml files
 * outputDirectory - directory to store generated java files
 * JAX-RSVersion default 1.1
 * useJsr303Annotations should we use Jsr301 or not
 * jsonMapper version of json mapper to use defaults to 'jackson1'

##Using Java API to generate JAX-RS on the fly:

Actually API developers should seldom have to use the core generator but instead should use a plug-in for their build tool. But there is an example of how to do it:

###Example:

    File outputDirectory = new File("/some/path/to/target/code-gen");

    Configuration configuration = new Configuration();
    configuration.setOutputDirectory(outputDirectory);
    configuration.setSourceDirectory(inputDirectory)
    configuration.setBasePackageName("org.raml.jaxrs.test");

    InputStreamReader ramlReader = new InputStreamReader(getClass().getResourceAsStream("/org/raml/full-config-with-patch.yaml"));

    new Generator().run(ramlReader, configuration);

