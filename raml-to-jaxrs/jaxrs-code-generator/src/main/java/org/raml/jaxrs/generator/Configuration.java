package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.builders.extensions.types.GsonExtension;
import org.raml.jaxrs.generator.builders.extensions.types.JacksonBasicExtension;
import org.raml.jaxrs.generator.builders.extensions.types.JacksonExtensions;
import org.raml.jaxrs.generator.builders.extensions.types.JavadocTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.types.JaxbTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.types.Jsr303Extension;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/25/16.
 * Just potential zeroes and ones
 */
public class Configuration {


    private final Map<String, String> props;

    public static Configuration createConfiguration(String configString) {

        Map<String, String> props = parseString(configString);

        return new Configuration(props);
    }

    private static Map<String, String> parseString(String configString) {

        Map<String, String> props = new HashMap<>();
        if ( configString == null ) {

            return props;
        }

        String[] propArray = configString.split(",");
        for (String prop : propArray) {

            String[] pair = prop.split("=");
            if ( pair.length == 1) {
                props.put(pair[0], "true");
            } else {

                props.put(pair[0], pair[1]);
            }
        }

        return props;
    }

    public Configuration(Map<String, String> props) {

        this.props = props;
    }



    public void setupBuild(CurrentBuild build) {

        if ( props.containsKey("useJackson")) {

            build.addExtension(new JacksonExtensions());
        }

        if ( props.containsKey("useJsr303")) {

            build.addExtension(new Jsr303Extension());
        }

        if ( props.containsKey("useGson")) {

            build.addExtension(new GsonExtension());
        }

        if ( props.containsKey("useJaxb")) {

            build.addExtension(new JaxbTypeExtension());
        }

        if ( props.containsKey("useJavadoc")) {

            build.addExtension(new JavadocTypeExtension());
        }
    }

}
