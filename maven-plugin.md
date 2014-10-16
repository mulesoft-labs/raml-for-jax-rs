![](http://raml.org/images/logo.png)

#Maven Plug-ins

##RAML to JAX-RS

This Maven plug-in generates JAX-RS annotated interfaces and supporting classes based on one or multiple RAML files.

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
If no, create it. The following one is an empty `settings.xml` template:
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
        <JAX-RSVersion>2.0</JAX-RSVersion>
        <useJsr303Annotations>false</useJsr303Annotations>
        <!-- Valid values: jackson1 jackson2 gson none -->
        <jsonMapper>jackson2</jsonMapper>
        <removeOldOutput>true</removeOldOutput>
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
We are working on the MVN plug-in for the RAML generation.
