/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.parser.visitor;

import static org.raml.parser.tagresolver.IncludeResolver.SEPARATOR;

import org.raml.parser.tagresolver.IncludeResolver;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class IncludeInfo
{

    private int line;
    private int startColumn;
    private int endColumn;
    private String includeName;

    public IncludeInfo(int line, int startColumn, int endColumn, String includeName)
    {
        this.line = line;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.includeName = includeName;
    }

    public IncludeInfo(Mark startMark, Mark endMark, String includeName)
    {
        this(startMark.getLine(), startMark.getColumn(), endMark.getColumn(), includeName);
    }

    public IncludeInfo(ScalarNode node)
    {
        this(node.getStartMark(), node.getEndMark(), node.getValue());
    }

    public IncludeInfo(Tag tag)
    {
        StringBuilder encodedInclude = new StringBuilder(tag.getValue());
        endColumn = popTrailingNumber(encodedInclude);
        startColumn = popTrailingNumber(encodedInclude);
        line = popTrailingNumber(encodedInclude);
        includeName = encodedInclude.substring(IncludeResolver.INCLUDE_APPLIED_TAG.length());
    }

    private int popTrailingNumber(StringBuilder encodedInclude)
    {
        int idx = encodedInclude.lastIndexOf(SEPARATOR);
        int result = Integer.parseInt(encodedInclude.substring(idx + 1));
        encodedInclude.delete(idx, encodedInclude.length());
        return result;
    }

    public int getLine()
    {
        return line;
    }

    public int getStartColumn()
    {
        return startColumn;
    }

    public int getEndColumn()
    {
        return endColumn;
    }

    public String getIncludeName()
    {
        return includeName;
    }
}
