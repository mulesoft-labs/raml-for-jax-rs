## Maven plugin examples
The examples included in this folder all come with a server that can be started using `mvn exec:java`.

Here's an example configuration:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.raml.jaxrs</groupId>
            <artifactId>raml-to-jaxrs-maven-plugin</artifactId>
            <version>$VERSION</version>
            <dependencies>
                <dependency>
                    <groupId>org.raml.jaxrs</groupId>
                    <artifactId>jaxrs-code-generator</artifactId>
                    <version>$VERSION</version>
                </dependency>
            </dependencies>
            <configuration>
                <ramlFile>${project.build.resources[0].directory}/types_user_defined.raml</ramlFile>
                <resourcePackage>example.resources</resourcePackage>
                <modelPackage>example.model</modelPackage>
                <supportPackage>example.support</supportPackage>
                <generateTypesWith>
                    <value>jackson</value>
                </generateTypesWith>
            </configuration>
        </plugin>
    </plugins>
</build>
```

The `ramlFile` configuration parameter should point to your RAML file.

The `resourcePackage` is the name of the resource package. This parameter is mandatory,
and serves as the default value for the modelPackage and supportPackage parameters,
should they be undefined.  It determines the Java package in which the classes will appear.

The `modelPackage` configuration parameter determines the package in which Java package the types
will be generated.

The `supportPackage` configuration parameter determines the package in which Java package
the support classes (serializers, responses and such) will be generated.

The `generateTypesWith` configuration parameter determines which annotation plugin(s) to use
to generate types. Available options are: jackson, gson, jaxb, javadoc, jsr303

Generate JAX-RS code by running:
```
mvn raml:generate
```
