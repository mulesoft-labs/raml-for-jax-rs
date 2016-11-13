package org.raml.utilities;

import com.google.common.base.Strings;

import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@NotThreadSafe
public class IndentedAppendable implements Appendable {

    private static final String END_OF_LINE = System.lineSeparator();

    private final String indent;
    private final Appendable appendable;
    private String currentIndent = "";

    private IndentedAppendable(String indent, Appendable appendable) {
        this.indent = indent;
        this.appendable = appendable;
    }

    public static IndentedAppendable forNoSpaces(int noSpaces, Appendable appendable) {
        checkArgument(noSpaces >= 0);
        checkNotNull(appendable);

        return new IndentedAppendable(Strings.repeat(" ", noSpaces), appendable);
    }

    public void indent() {
        currentIndent += indent;
    }

    public void outdent() {
        checkState(!currentIndent.isEmpty(), "outdenting one too many times");

        currentIndent = currentIndent.substring(0, currentIndent.length() - indent.length());
    }

    public IndentedAppendable withIndent() throws IOException {
        this.appendable.append(currentIndent);
        return this;
    }

    public IndentedAppendable appendLine(String content) throws IOException {
        this.appendable.append(currentIndent).append(content).append("\n");
        return this;
    }

    @Override
    public IndentedAppendable append(CharSequence csq) throws IOException {
        this.appendable.append(csq);
        return this;
    }

    @Override
    public IndentedAppendable append(CharSequence csq, int start, int end) throws IOException {
        this.appendable.append(csq, start, end);
        return this;
    }

    @Override
    public IndentedAppendable append(char c) throws IOException {
        this.appendable.append(c);
        return this;
    }

    public IndentedAppendable endOfLine() throws IOException {
        this.appendable.append(END_OF_LINE);
        return this;
    }

    public String toString() {
        return this.appendable.toString();
    }
}
