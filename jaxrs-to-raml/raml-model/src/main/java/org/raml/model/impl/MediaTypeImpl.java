package org.raml.model.impl;

import org.raml.model.MediaType;

import static org.raml.utilities.contract.Preconditions.checkWord;

public class MediaTypeImpl implements MediaType {
    private final String string;

    private MediaTypeImpl(String string) {
        this.string = string;
    }

    public static MediaTypeImpl create(String string) {
        checkWord(string);

        return new MediaTypeImpl(string);
    }

    @Override
    public String toString() {
        return this.string;
    }
}
