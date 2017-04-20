## JAXB annotations
JAXB annotations are pretty simply used. There is an example project [here](examples/maven-examples/jaxb-example).

### Properties
Annotation | Placement |
-----------|:----------|
`@XmlAccessorType` | The JAXB plugin annotates property fields. |
`@XmlRootElement` | Both interface and implementation are annotated as element roots. |
`@XmlElement` | All properties are annotated with this. |

### Inheritance
Currently, inheritance is handled through JAXB.  There are no discriminator values.
The example used uses MOXy, and requires no special annotation.

### Unions
Unions are handled through a union class annotated as a simple JAXB object.
Custom serializers are not necessary.
