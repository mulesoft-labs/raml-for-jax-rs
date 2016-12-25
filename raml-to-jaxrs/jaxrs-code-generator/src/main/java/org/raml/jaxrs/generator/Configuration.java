package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.builders.extensions.types.GsonExtension;
import org.raml.jaxrs.generator.builders.extensions.types.JacksonExtension;
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
        Configuration config = new Configuration(props);

        return config;
    }

    private static Map<String, String> parseString(String configString) {

        Map<String, String> props = new HashMap<>();
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

            build.addExtension(new JacksonExtension());
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
