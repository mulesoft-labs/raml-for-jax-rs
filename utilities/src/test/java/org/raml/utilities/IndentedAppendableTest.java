package org.raml.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by barnabef on 3/31/17.
 */
public class IndentedAppendableTest {
    @Test
    public void testEscaping() {
        assertStringIsCorrectlyEscaped("title", "[This is a test]", "title: \"[This is a test]\"");
        assertStringIsCorrectlyEscaped("version", "#1.0", "version: \"#1.0\"");
        assertStringIsCorrectlyEscaped("baseUri", "http://www.test.com/test?test=true#hash", "baseUri: http://www.test.com/test?test=true#hash"); // No quotes if not first character
        assertStringIsCorrectlyEscaped("mediaType", "*/*", "mediaType: \"*/*\"");
        assertStringIsCorrectlyEscaped("description", "|This is a description with \"double quotes\" but with control character","description: \"|This is a description with \\\"double quotes\\\" but with control character\"");
        assertStringIsCorrectlyEscaped("description", "This is a description with lots of characters that are not escaped: *|#{}?:,[]", "description: This is a description with lots of characters that are not escaped: *|#{}?:,[]");
        assertStringIsCorrectlyEscaped("description","{This is a description with control character}", "description: \"{This is a description with control character}\"");

    }

    public void assertStringIsCorrectlyEscaped(String tag, String value, String expected) {
        StringBuffer buffer = new StringBuffer();
        IndentedAppendable appendable = IndentedAppendable.forNoSpaces(1, buffer);
        try {
            appendable.appendEscapedLine(tag, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(expected, buffer.toString().trim());
    }
}
