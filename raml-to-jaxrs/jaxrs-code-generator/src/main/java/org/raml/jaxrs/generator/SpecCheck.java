package org.raml.jaxrs.generator;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public class SpecCheck {

    public static void main(String[] args) {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(args[0]);
        if (ramlModelResult.hasErrors())
        {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults())
            {
                System.err.println(validationResult.toString());
            }
        }
        else
        {
            Api api = ramlModelResult.getApiV10();
            if ( api != null ) {
                System.err.println("spec parsed");
            } else {

                System.err.println("spec did not parse");
            }
        }
    }
}
