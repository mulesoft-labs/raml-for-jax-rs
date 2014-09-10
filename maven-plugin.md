![](http://raml.org/images/logo.png)

# Maven Plug-ins

## JAX-RS to RAML

Coming soon.


##RAML to JAX-RS

This plug-in generates JAX-RS annotated interfaces and supporting classes based on one or multiple RAML files.

_NB. The following documentation will soon be superseded by the Maven-generated plug-in documentation._

### Usage

Add `<pluginGroup>org.raml.plugins</pluginGroup>` to the `pluginGroups` section of your Maven `settings.xml`.

In your `pom.xml`, add the following build plug-in:

    <plugin>
        <groupId>org.raml.plugins</groupId>
        <artifactId>raml-JAX-RS-maven-plugin</artifactId>
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

Now if you will run mvn compile or mvn package raml file will be processed and you will able to see generated classes as well as java source files in generated-sources folder.

Consideration: m4e did not handles this well yet.
