package org.raml.utilities.builder;

import static com.google.common.base.Preconditions.checkState;

public class Preconditions {

    public static <T> void checkSet(Field<T> field, String fieldName) {
        checkState(field.isSet(), "%s expected to be set", fieldName);
    }

    public static <T> void checkUnset(Field<T> field, String fieldName) {
        if (field.isSet()) {
            checkState(false, "%s expected to be unset to set to %s", fieldName, field.get());
        }
    }

}
