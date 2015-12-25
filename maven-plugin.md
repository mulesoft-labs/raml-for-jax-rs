![](http://raml.org/images/logo.png)

#Maven Plug-ins

##RAML to JAX-RS

This Maven plug-in generates JAX-RS annotated interfaces and supporting classes based on one or multiple RAML files.

### Maven artifacts
Maven artifacts are available at:

 - https://repository-master.mulesoft.org/releases/ - release repository
 - https://repository-master.mulesoft.org/snapshots/ - snaphots repository

as well as at Maven Central (http://mvnrepository.com/artifact/org.raml)

###Usage

You must include the plug-in in your project's pom.xml. For example:

```xml
<plugin>
    <groupId>org.raml.plugins</groupId>
    <artifactId>raml-jaxrs-maven-plugin</artifactId>
    <version>1.3.4</version>
    <configuration>
        <!-- Use sourcePaths if you want to provide a single RAML file or a list of RAML files -->
        <sourceDirectory>${basedir}/raml</sourceDirectory>
        <!-- Optionally configure outputDirectory if you don't like the default value: ${project.build.directory}/generated-sources/raml-JAX-RS -->
        <!-- Replace with your package name -->
        <basePackageName>com.acme.api</basePackageName>
        <!-- Valid values: 1.1 2.0 -->
        <JAX-RSVersion>2.0</JAX-RSVersion>
        <useJsr303Annotations>false</useJsr303Annotations>
        <!-- Valid values: jackson1 jackson2 gson none -->
        <jsonMapper>jackson2</jsonMapper>
        <removeOldOutput>true</removeOldOutput>
        <!-- Optionally set extensions to a list of fully qualified names of classes
        that implement org.raml.jaxrs.codegen.core.ext.GeneratorExtension -->
        <!-- for example:
        <extensions>
            <param>com.abc.AuthorizationAnnotationExtension</param>
            <param>com.abc.ParameterFilterExtension</param>
        </extensions>
        Custom annotator for json schema to pojo convertor
        <customAnnotator>com.abc.MyCustomAnnotator</customAnnotator>
        -->
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <phase>generate-sources</phase>
        </execution>
    </executions>
</plugin>
```

The RAML definition will be processed and the code will be generated when running `mvn raml:generate`. 
Also it'll be executed during `mvn compile` or `mvn package`.

## JAX-RS to RAML

This Maven plug-in generates a RAML file based on JAX-RS annotated interfaces and supporting classes.

###Usage

You must include the plug-in in your project's pom.xml. For example, add the following element to `project/build/plugins`:

```xml
<plugin>
	<groupId>org.raml.plugins</groupId>
	<artifactId>jaxrs-raml-maven-plugin</artifactId>
	<version>1.3.4</version>
	<configuration>
		<sourcePaths>
			<param>${basedir}/src/main/java/contacts/Contact.java</param>
			<param>${basedir}/src/main/java/contacts/ContactAttrs.java</param>
			<param>${basedir}/src/main/java/contacts/Contacts.java</param>
		</sourcePaths>
		<sourceDirectory>${basedir}/src/main/java</sourceDirectory>
		<outputFile>${project.build.directory}/generated-sources/jaxrs-raml/example.raml</outputFile>
		<removeOldOutput>true</removeOldOutput>
	</configuration>
	<executions>
		<execution>
			<goals>
				<goal>generate-raml</goal>
			</goals>
			<phase>process-classes</phase>
		</execution>
	</executions>
</plugin>
```
The RAML definition will be processed and the code will be generated when running `mvn compile` or `mvn package`.
####Parameters:
- sourcePaths: List of absolute o relative paths to source files to be processed. All files must be on the project's classpath. If the parameter is omited, the sourceDirectory parameter is used.
- sourceDirectory: Absolute o relative path to source folder to be processed. The folder must be on the project's classpath. Default value is `${basedir}/src/main/java`.
- removeOldOutput: Whether to clean or not the output directory before generation. Default value is `false`.
- outputFile: Absolute or relative output loaction. If you specify a directory here then it is considered output directory, raml content is saved to `api.raml` file inside it, schema and examples are generated into corresponding subfolders. If you specify a file, then output directory is set to it's parent directory. Default value is `${project.build.directory}/generated-sources/jaxrs-raml/api.raml`.

####Eclipse usage

When developing in Eclipse, you must manage lifecycle mapping. For this purpose your `pom.xml` must have the following element inside `project/build/pluginManagement/plugins`:
``` xml
<plugin>
	<groupId>org.eclipse.m2e</groupId>
	<artifactId>lifecycle-mapping</artifactId>
	<version>1.3.4</version>
	<configuration>
		<lifecycleMappingMetadata>
			<pluginExecutions>
			</pluginExecutions>
		</lifecycleMappingMetadata>
	</configuration>
</plugin>
```
Create it if you do not already have one and add the following child element to `pluginExecutions`:
``` xml
<pluginExecution>
	<pluginExecutionFilter>
		<groupId>org.raml.plugins</groupId>
		<artifactId>jaxrs-raml-maven-plugin</artifactId>
		<versionRange>1.3.4</versionRange>
		<goals>
			<goal>generate-raml</goal>
		</goals>
	</pluginExecutionFilter>
	<action>
		<execute>
			<runOnIncremental>false</runOnIncremental>
		</execute>
	</action>
</pluginExecution>
```
