![](http://raml.org/images/logo.png)

# RAML JAX-RS Codegen - Core Generator


Commandline Installation Instructions

You can download the command tool jar from http://raml-tools.mulesoft.com/jaxrs-to-raml/javac/ramlToJaxrs.jar, or you can clone this repository and build it locally by following these steps:

Go to the project root folder.
cd /raml-to-jaxrs/core/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
cd ../com.mulesoft.jaxrs.raml.generator.annotations
cd ../com.mulesoft.jaxrs.raml.generator.annotations/target
Inside this folder, you will find the generated jar (raml-jaxrs-codegen-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar).
This jar file (downloaded or built yourself) contains all that you need to run RAML-to-JAXRS as a command line

Usage:

To do it you should type following commandline 

java -cp raml-jaxrs-codegen-core-1.0-SNAPSHOT-jar-with-dependencies.jar  org.raml.jaxrs.codegen.core.Launcher

and add options there:

API developers should seldom have to use the core generator but instead should use a plug-in for their build tool.

## Usage

Example:

    File outputDirectory = new File("/some/path/to/target/code-gen");

    Configuration configuration = new Configuration();
    configuration.setOutputDirectory(outputDirectory);
    configuration.setSourceDirectory(inputDirectory)
    configuration.setBasePackageName("org.raml.jaxrs.test");

    InputStreamReader ramlReader = new InputStreamReader(getClass().getResourceAsStream("/org/raml/full-config-with-patch.yaml"));

    new Generator().run(ramlReader, configuration);

