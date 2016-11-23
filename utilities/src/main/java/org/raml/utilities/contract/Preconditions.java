package org.raml.utilities.contract;

import static com.google.common.base.Preconditions.checkArgument;

public class Preconditions {
    private Preconditions() {}

    public static String checkWord(String string) {
        checkArgument(!string.trim().isEmpty());
        return string;
    }

    public static String checkWord(String string, String message, Object... formattedArguments) {
        checkArgument(!string.trim().isEmpty(), message, formattedArguments);
        return string;
    }
}
