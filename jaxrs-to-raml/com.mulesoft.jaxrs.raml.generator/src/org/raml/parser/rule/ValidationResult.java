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

/**
 * <p>ValidationResult class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ValidationResult
{

    /** Constant <code>UNKNOWN=-1</code> */
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

    /**
     * <p>Getter for the field <code>level</code>.</p>
     *
     * @return a {@link org.raml.parser.rule.ValidationResult.Level} object.
     */
    public Level getLevel()
    {
        return level;
    }

    /**
     * <p>Getter for the field <code>message</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * <p>Getter for the field <code>line</code>.</p>
     *
     * @return a int.
     */
    public int getLine()
    {
        return line;
    }

    /**
     * <p>Getter for the field <code>startColumn</code>.</p>
     *
     * @return a int.
     */
    public int getStartColumn()
    {
        return startColumn;
    }

    /**
     * <p>Getter for the field <code>endColumn</code>.</p>
     *
     * @return a int.
     */
    public int getEndColumn()
    {
        return endColumn;
    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid()
    {
        return level != Level.ERROR;
    }

    /**
     * <p>getIncludeName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIncludeName()
    {
        if (includeContext.isEmpty())
        {
            return null;
        }
        return includeContext.peek().getIncludeName();
    }

    /**
     * <p>Getter for the field <code>includeContext</code>.</p>
     *
     * @return a {@link java.util.Deque} object.
     */
    public Deque<IncludeInfo> getIncludeContext()
    {
        return includeContext;
    }

    /**
     * <p>Setter for the field <code>includeContext</code>.</p>
     *
     * @param includeContext a {@link java.util.Deque} object.
     */
    public void setIncludeContext(Deque<IncludeInfo> includeContext)
    {
        this.includeContext = new ArrayDeque<IncludeInfo>(includeContext);
    }

    /**
     * <p>addIncludeContext.</p>
     *
     * @param includeContext a {@link java.util.Deque} object.
     */
    public void addIncludeContext(Deque<IncludeInfo> includeContext)
    {
        this.includeContext.addAll(includeContext);
    }

    /**
     * <p>areValid.</p>
     *
     * @param validationResults a {@link java.util.List} object.
     * @return a boolean.
     */
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

    /**
     * <p>Getter for the field <code>level</code>.</p>
     *
     * @param level a {@link org.raml.parser.rule.ValidationResult.Level} object.
     * @param results a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
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

    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return "ValidationResult{" +
               "level=" + level +
               ", message='" + message + '\'' +
               '}';
    }

    /**
     * <p>createErrorResult.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param line a int.
     * @param startIndex a int.
     * @param endIndex a int.
     * @return a {@link org.raml.parser.rule.ValidationResult} object.
     */
    public static ValidationResult createErrorResult(String message, int line, int startIndex, int endIndex)
    {
        return new ValidationResult(Level.ERROR, message, line, startIndex, endIndex);
    }

    /**
     * <p>createErrorResult.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param startMark a {@link org.yaml.snakeyaml.error.Mark} object.
     * @param endMark a {@link org.yaml.snakeyaml.error.Mark} object.
     * @return a {@link org.raml.parser.rule.ValidationResult} object.
     */
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

    /**
     * <p>createErrorResult.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param node a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @return a {@link org.raml.parser.rule.ValidationResult} object.
     */
    public static ValidationResult createErrorResult(String message, Node node)
    {
        return createErrorResult(message, node.getStartMark(), node.getEndMark());
    }

    /**
     * <p>createErrorResult.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @return a {@link org.raml.parser.rule.ValidationResult} object.
     */
    public static ValidationResult createErrorResult(String message)
    {
        return createErrorResult(message, UNKNOWN, UNKNOWN, UNKNOWN);
    }

    /**
     * <p>create.</p>
     *
     * @param level a {@link org.raml.parser.rule.ValidationResult.Level} object.
     * @param message a {@link java.lang.String} object.
     * @return a {@link org.raml.parser.rule.ValidationResult} object.
     */
    public static ValidationResult create(Level level, String message)
    {
        return new ValidationResult(level, message, UNKNOWN, UNKNOWN, UNKNOWN);
    }
}
