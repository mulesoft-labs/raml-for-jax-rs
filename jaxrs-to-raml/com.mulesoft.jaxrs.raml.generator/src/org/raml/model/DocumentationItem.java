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
package org.raml.model;

import org.raml.parser.annotation.Scalar;

/**
 * <p>DocumentationItem class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class DocumentationItem
{
    @Scalar(required = true)
    private String title;

    @Scalar(required = true)
    private String content;
    
    private String origin;

    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getContent()
    {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link java.lang.String} object.
     */
    public void setContent(String content)
    {
        this.content = content;
    }

	/**
	 * <p>Getter for the field <code>origin</code>.</p>
	 *
	 * @return name of the file where item is stored (null if in the same file)
	 */
	public String getOrigin() {
		return origin;
	}
}
