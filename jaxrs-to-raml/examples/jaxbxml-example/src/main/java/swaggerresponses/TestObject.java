package swaggerresponses;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="customer")
@XmlType(name="AA")
 public class TestObject {

   
      @XmlAttribute
      int x;
      
      @XmlElement      
      byte[]imageData;
      
      @XmlAttribute
      boolean married;

      /**
       * The human readable name of the customer.
       */
      @XmlElement(required=true)
      private String name;

      /**
       * The (SNT) code for this customer.
       */
      @XmlElement
      private String sntCode;

      /**
       * The HREF to the VDCs associated with this customer.
       */
      @XmlElement
      private String vdc;
      
      @XmlElement(required=true)
      protected ArrayList<TestInnerObject>children;
      
      @XmlElement(required=true)
      private AnotherNamespaceObject rpp;
 }