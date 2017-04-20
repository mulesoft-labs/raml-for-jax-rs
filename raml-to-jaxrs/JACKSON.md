## Jackson annotations
Currently, Jackson annotations are added similarly to how [`jsonschema2pojo`](https://github.com/joelittlejohn/jsonschema2pojo) handles its objects. There is an example project [here](examples/maven-examples/raml-defined-example).

### Properties
Annotation | Placement |
-----------|:----------|
`@JsonInclude(JsonInclude.Include.NON_NULL)` | Placed on all types
`@JsonPropertyOrder()` | Placed on all types
`@JsonProperty("property")` |  These annotations are added to the field, getters and setters on the implementation of the class.
`@JsonAnyGetter` | An `additionalProperties` map is added and annotated
`@JsonIgnore` | The `additionalProperties` map is marked as ignored as it's handled by Jackson

Scalar types, specifically dates, have different annotations put on them depending on their RAML definition.
`@JsonFormat`, `@JsonFormat.Shape` are added whith these rules.

### Dates
RAML date format | Format |
-----------|:----------|
datetime-only | yyyy-MM-dd'T'HH:mm:ss.SSSZ |
time | HH:mm:ss |
date | yyyy-MM-dd |
datetime, rfc2616 | EEE, dd MMM yyyy HH:mm:ss z|
datetime, other | yyyy-MM-dd'T'HH:mm:ssZ |

### Inheritance
Currently, inheritance is handled through a discriminator value.

Types in the hierarchy's interface declarations are annotated with the following annotations:

Annotation |  Object |
-------------|:----------|
`@JsonTypeInfo` | Marks the interface with with the discriminator value for a given type
`@JsonSubTypes` | Enumerates all the subtypes of the given type.
`@JsonDeserialize` | This is used to tie interfaces to their implementations

### Unions
Unions are handled through a union class using a custom serializer that tries to discover the actual Java type of the object by
examining the properties of the JSON object.
