package org.raml.emitter;

import java.nio.file.Path;

public interface Emitter {
    void emit(Path whereTo) throws RamlEmissionException;
}
