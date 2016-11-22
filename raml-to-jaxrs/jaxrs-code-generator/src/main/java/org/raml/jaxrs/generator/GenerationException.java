package org.raml.jaxrs.generator;

import org.raml.v2.api.model.common.ValidationResult;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * Just potential zeroes and ones
 */
public class GenerationException extends RuntimeException {
    public GenerationException(List<ValidationResult> validationResults) {
        super(validationResults.toString());
    }

    public GenerationException(Exception e) {
        super(e);
    }

    public GenerationException(String s) {
        super(s);
    }
}
