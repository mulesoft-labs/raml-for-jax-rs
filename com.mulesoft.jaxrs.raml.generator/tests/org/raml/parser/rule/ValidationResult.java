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
package org.raml.parser.rule;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.raml.parser.visitor.IncludeInfo;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class ValidationResult
{

    public static int UNKNOWN = -1;

    public enum Level
    {
        ERROR, WARN, INFO
    }

    private Level level;
    private String message;
    private int line;
    private int startColumn;
    private int endColumn;
    private Deque<IncludeInfo> includeContext = new ArrayDeque<IncludeInfo>();

    private ValidationResult(Level level, String message, int line, int startColumn, int endColumn)
    {
        this.level = level;
        this.message = message;
        this.line = line;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    public Level getLevel()
    {
        return level;
    }

    public String getMessage()
    {
        return message;
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

    public boolean isValid()
    {
        return level != Level.ERROR;
    }

    public String getIncludeName()
    {
        if (includeContext.isEmpty())
        {
            return null;
        }
        return includeContext.peek().getIncludeName();
    }

    public Deque<IncludeInfo> getIncludeContext()
    {
        return includeContext;
    }

    public void setIncludeContext(Deque<IncludeInfo> includeContext)
    {
        this.includeContext = new ArrayDeque<IncludeInfo>(includeContext);
    }

    public void addIncludeContext(Deque<IncludeInfo> includeContext)
    {
        this.includeContext.addAll(includeContext);
    }

    public static boolean areValid(List<ValidationResult> validationResults)
    {
        for (ValidationResult result : validationResults)
        {
            if (!result.isValid())
            {
                return false;
            }
        }
        return true;
    }

    public static List<ValidationResult> getLevel(Level level, List<ValidationResult> results)
    {
        List<ValidationResult> filtered = new ArrayList<ValidationResult>();
        for (ValidationResult result : results)
        {
            if (result.level == level)
            {
                filtered.add(result);
            }
        }
        return filtered;
    }

    @Override
    public String toString()
    {
        return "ValidationResult{" +
               "level=" + level +
               ", message='" + message + '\'' +
               '}';
    }

    public static ValidationResult createErrorResult(String message, int line, int startIndex, int endIndex)
    {
        return new ValidationResult(Level.ERROR, message, line, startIndex, endIndex);
    }

    public static ValidationResult createErrorResult(String message, Mark startMark, Mark endMark)
    {
    	if (startMark==null){
    		startMark=new Mark("", -1, -1, -1, "", 1);
    	}
    	if (endMark==null){
    		endMark=new Mark("", -1, -1, -1, "", 1);
    	}
        return createErrorResult(message, startMark.getLine(), startMark.getColumn(), endMark.getColumn());
    }

    public static ValidationResult createErrorResult(String message, Node node)
    {
        return createErrorResult(message, node.getStartMark(), node.getEndMark());
    }

    public static ValidationResult createErrorResult(String message)
    {
        return createErrorResult(message, UNKNOWN, UNKNOWN, UNKNOWN);
    }

    public static ValidationResult create(Level level, String message)
    {
        return new ValidationResult(level, message, UNKNOWN, UNKNOWN, UNKNOWN);
    }
}
