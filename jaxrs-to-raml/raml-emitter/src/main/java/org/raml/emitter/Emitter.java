package org.raml.emitter;

import org.raml.model.RamlApi;

public interface Emitter {
    void emit(RamlApi api) throws RamlEmissionException;
}
