package org.raml.jaxrs.generator.matchers;

import com.squareup.javapoet.CodeBlock;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * Created by Jean-Philippe Belanger on 3/4/17.
 * Just potential zeroes and ones
 */
public class CodeBlockMatchers {


    public static Matcher<CodeBlock> codeBlockContents(Matcher<? super String> codeBlock) {

        return new FeatureMatcher<CodeBlock, String>(codeBlock, "contents", "contents") {
            @Override
            protected String featureValueOf(CodeBlock codeBlock) {
                return codeBlock.toString();
            }
        };
    }
}
