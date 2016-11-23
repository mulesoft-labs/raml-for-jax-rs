package org.raml.utilities.format;

public class Joiners {
    private static final Joiner SQUARE_BRACKETS_PER_LINE_JOINER = Joiner.on(",\n  ").withPrefix("[\n  ").withSuffix("\n]").ifEmpty("[]");

    public static Joiner squareBracketsPerLineJoiner() {
        return SQUARE_BRACKETS_PER_LINE_JOINER;
    }
}
