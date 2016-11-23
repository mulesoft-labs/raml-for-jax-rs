package org.raml.model.impl;

import org.raml.model.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.utilities.contract.Preconditions.checkWord;

public class MediaTypeImpl implements MediaType {
    private final String type;
    private final String subType;
    private final String string;

    private MediaTypeImpl(String type, String subType, String string) {
        this.type = type;
        this.subType = subType;
        this.string = string;
    }

    public static MediaTypeImpl create(String type, String subType, String string) {
        checkWord(type);
        checkWord(subType);
        checkWord(string);

        return new MediaTypeImpl(type, subType, string);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getSubType() {
        return this.subType;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
