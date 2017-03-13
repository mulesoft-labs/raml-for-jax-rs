# Using the RAML type generation plugins

The first thing you will need to do to hook yourself into the code generation features is to build a project with the plugin classes.
The example project in our case is [features-plugins](feature-plugins) project.  Its [pom.xml](feature-plugins/pom.xml) is a straight
java jar project. The only required dependency is this:
```xml
    <dependencies>
        <dependency>
            <groupId>org.raml</groupId>
            <artifactId>jaxrs-code-generator</artifactId>
            <version>2.0.0-RC2-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

Secondly, we need to setup the [raml project](feature-raml-project) per sé.  Its [pom.xml](feature-raml-project/pom.xml) needs to define
the plugin with a dependency to our plugin code.

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.raml</groupId>
                <artifactId>raml-to-jaxrs-maven-plugin</artifactId>
                <version>2.0.0-RC2-SNAPSHOT</version>
                <dependencies>
                    <dependency>
                        <groupId>org.raml</groupId>
                        <artifactId>feature-plugins</artifactId>
                        <version>2.0.0-RC2-SNAPSHOT</version>
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

We are ready to run our project.

# Examples

## Example #1: adding a @deprecated annotation
Firstly, write the [code](feature-plugins/src/main/java/org/raml/jaxrs/features/AddDeprecatedAnnotationToTypePlugin) that will add the annotation. The code generator uses JavaPoet to generate the java code.
``` java
public class AddDeprecatedAnnotationToTypePlugin implements TypeExtension {

  @Override
  public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase btype) {

    builder.addAnnotation(AnnotationSpec.builder(Deprecated.class).build());
    return builder;
  }
}
```
Secondly, in your [raml project](feature-raml-project/src/main/resources/simple-example-types.raml), import the `ramltojaxrs` annotations:

``` yaml
uses:
  ramltojaxrs: ramltojaxrs.raml
```

Annotate the type you need to deprecate:
```yaml
types:
    Network:
        (ramltojaxrs.types):
            onTypeCreation: [org.raml.jaxrs.features.AddDeprecatedAnnotationToTypePlugin]
        type: object
        properties:
            name:
                required: true
                type: string
            dns-address:
                required: true
                type: string
```
Both the implementation and declaration will be marked `@Deprecated`

## Example #2: suppressing the generation of a type
Plugins that return null values suppress the generation of that item.

The Switch type in our [raml project](feature-raml-project/src/main/resources/simple-example-types.raml) is suppressed,
as the org.raml.jaxrs.features.SuppressTypePlugin returns null.
