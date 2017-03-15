package org.raml.jaxrs.generator.matchers;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * Created by Jean-Philippe Belanger on 3/15/17.
 * Just potential zeroes and ones
 */
public class ParameterSpecMatchers {

    public static Matcher<ParameterSpec> type(Matcher<TypeName> match) {

        return new FeatureMatcher<ParameterSpec, TypeName>(match, "type name", "type name") {

            @Override
            protected TypeName featureValueOf(ParameterSpec actual) {
                return actual.type;
            }
        };
    }

}
