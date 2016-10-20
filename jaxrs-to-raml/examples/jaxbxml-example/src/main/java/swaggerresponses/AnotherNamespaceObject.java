package swaggerresponses;
import javax.xml.bind.annotation.*;

@XmlRootElement(namespace="http://www.example.com2")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnotherNamespaceObject {

    String foo;

    @XmlElement(namespace="http://www.example.com")
    String bar;

}