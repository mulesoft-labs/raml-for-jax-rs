## Using the generation plugins
The first thing you will need to do to hook yourself into the code generation features is to build a project with the plugin classes.
The example project in our case is called [feature-plugins](feature-plugins/). Its [pom.xml](feature-plugins/pom.xml) is a straight
java jar project. The only required dependency is this:

```xml
<dependencies>
    <dependency>
        <groupId>org.raml.jaxrs</groupId>
        <artifactId>jaxrs-code-generator</artifactId>
        <version>2.x.y</version>
    </dependency>
</dependencies>
```

Secondly, we need to setup the [RAML project](feature-raml-project/) per sé.  Its [pom.xml](feature-raml-project/pom.xml) needs to define
the plugin with a dependency to our plugin code.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.raml.jaxrs</groupId>
            <artifactId>raml-to-jaxrs-maven-plugin</artifactId>
            <version>2.x.y</version>
            <dependencies>
                <dependency>
                    <groupId>org.raml.jaxrs</groupId>
                    <artifactId>feature-plugins</artifactId>
                    <version>2.x.y</version>
                </dependency>
            </dependencies>
            <configuration>
                <ramlFile>${project.build.resources[0].directory}/simple-example-types.raml</ramlFile>
                <resourcePackage>features.resources</resourcePackage>
                <modelPackage>features.model</modelPackage>
                <resourcePackage>features.types</resourcePackage>
            </configuration>
        </plugin>
    </plugins>
</build>
```

We are now ready to run our project. There are several use-cases listed and explained [here](USE_CASES.md).
