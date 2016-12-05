package org.raml.emitter;

import org.raml.api.RamlApi;

public interface Emitter {
    void emit(RamlApi api) throws RamlEmissionException;
}
