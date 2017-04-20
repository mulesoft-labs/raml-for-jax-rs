## When to use the generation plugins

### Use case #1: adding a @deprecated annotation
Firstly, write the [code](feature-plugins/src/main/java/org/raml/jaxrs/features/AddDeprecatedAnnotationToTypePlugin.java) that will add the annotation. The code generator uses JavaPoet to generate the java code.

```java
public class AddDeprecatedAnnotationToTypePlugin implements TypeExtension {

  @Override
  public TypeSpec.Builder onType(TypeContext context, TypeSpec.Builder builder, V10GType type, BuildPhase btype) {

    builder.addAnnotation(AnnotationSpec.builder(Deprecated.class).build());
    return builder;
  }
}
```
Secondly, in your raml project [RAML file](feature-raml-project/src/main/resources/simple-example-types.raml), import the `ramltojaxrs` annotations:
```yaml
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

### Use case #2: suppressing the generation of a type
Plugins that return `null` values suppress the generation.
The `Switch` type in our [RAML file](feature-raml-project/src/main/resources/simple-example-types.raml) will be suppressed,
as [`org.raml.jaxrs.features.SuppressTypePlugin`](feature-plugins/src/main/java/org/raml/jaxrs/features/SuppressTypePlugin.java) returns `null`.

### Use case #3: chainable setters
The `ChainableRouter` type in our [RAML file](feature-raml-project/src/main/resources/simple-example-types.raml) uses the [`ChainSetter` extension](feature-plugins/src/main/java/org/raml/jaxrs/features/ChainSetter.java).
The idea is that, to replace the method, we use the JavaPoet builders, building the original builder, creating a new builder and then copying the important features, such as the parameters and annotations.
