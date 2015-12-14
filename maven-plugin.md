![](http://raml.org/images/logo.png)

#Maven Plug-ins

##RAML to JAX-RS

This Maven plug-in generates JAX-RS annotated interfaces and supporting classes based on one or multiple RAML files.

### Maven artifacts
Maven artifacts are available at:

 - https://repository-master.mulesoft.org/releases/ - release repository
 - https://repository-master.mulesoft.org/snapshots/ - snaphots repository

as well as at Maven Central (http://search.maven.org/#search%7Cga%7C1%7Corg.raml)

###Installation

In order to use this plug-in, it needs to be installed in the Maven Repositories. In order to do that:

- Clone this repository or download a snapshot.
- On the cloned repository, go to the `raml-to-jaxrs/core` folder, and execute `mvn install`
- Once installed, go to the `maven-plugin` folder and run `mvn install`.

The plug-in should now be installed. You can check your `.m2/repository/org/raml/plugins/raml-jaxrs-maven-plugin`:
```terminal
├── 1.0-SNAPSHOT
│   ├── _maven.repositories
│   ├── m2e-lastUpdated.properties
│   ├── maven-metadata-local.xml
│   ├── raml-jaxrs-maven-plugin-1.0-SNAPSHOT.jar
│   └── raml-jaxrs-maven-plugin-1.0-SNAPSHOT.pom
└── maven-metadata-local.xml

```

Once installed, add the `pluginGroup` to the Maven `settings.xml` This one, should be located in `.m2/settings.xml`.
If now, create it. The following one is an empty `settings.xml` template:
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
    http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository/>
    <interactiveMode/>
    <usePluginRegistry/>
    <offline/>
    <pluginGroups />
    <servers/>
    <mirrors/>
    <proxies/>
    <profiles/>
    <activeProfiles/>
</settings>
```
Add `<pluginGroup>org.raml.plugins</pluginGroup>` to the `pluginGroups` section.

###Usage

You must include the plug-in in your project's pom.xml. For example:

```xml
<plugin>
    <groupId>org.raml.plugins</groupId>
    <artifactId>raml-jaxrs-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <!-- Use sourcePaths if you want to provide a single RAML file or a list of RAML files -->
        <sourceDirectory>${basedir}/src/main/resources/raml</sourceDirectory>
        <!-- Optionally configure outputDirectory if you don't like the default value: ${project.build.directory}/generated-sources/raml-JAX-RS -->
        <!-- Replace with your package name -->
        <basePackageName>com.acme.api</basePackageName>
        <!-- Valid values: 1.1 2.0 -->
        <jaxrsVersion>2.0</jaxrsVersion>
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

The RAML definition will be processed and the code will be generated when running `mvn compile` or `mvn package`.

## JAX-RS to RAML

This Maven plug-in generates a RAML file based on JAX-RS annotated interfaces and supporting classes.

###Installation

In order to use this plug-in, it needs to be installed in the Maven Repositories. In order to do that:

- Clone this repository or download a snapshot.
- On the cloned repository, go to the `jaxrs-to-raml/com.mulesoft.jaxrs.raml.generator` folder, and execute `mvn install`
- Once installed, go to the `jaxrs-to-raml/jaxrs-raml-maven-plugin` folder and run `mvn install`.

The plug-in should now be installed. You can check your `.m2/repository/org/raml/plugins/jaxrs-raml-maven-plugin`:
```terminal
├── 1.0-SNAPSHOT
│   ├── _maven.repositories
│   ├── m2e-lastUpdated.properties
│   ├── maven-metadata-local.xml
│   ├── jaxrs-raml-maven-plugin-1.0-SNAPSHOT.jar
│   └── jaxrs-raml-maven-plugin-1.0-SNAPSHOT.pom
└── maven-metadata-local.xml

```

Once installed, add the `pluginGroup` to the Maven `settings.xml` just the same way as described in the RAML to JAX-RS case.

###Usage

You must include the plug-in in your project's pom.xml. For example, add the following element to `project/build/plugins`:

```xml
<plugin>
	<groupId>org.raml.plugins</groupId>
	<artifactId>jaxrs-raml-maven-plugin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
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
	<version>1.0.0</version>
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
		<versionRange>0.0.1-SNAPSHOT</versionRange>
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
