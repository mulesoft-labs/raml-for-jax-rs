package org.raml.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.builder.QuestionableActionTypeHandler;

/**
 * <p>ResourceType class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ResourceType extends Resource{

	@Mapping(handler = QuestionableActionTypeHandler.class, implicit = true)
    private Map<String, Action> questionedActions = new LinkedHashMap<String, Action>();

    /**
     * <p>Getter for the field <code>questionedActions</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Action> getQuestionedActions() {
		return questionedActions;
	}

	/**
	 * <p>Setter for the field <code>questionedActions</code>.</p>
	 *
	 * @param questionedActions a {@link java.util.Map} object.
	 */
	public void setQuestionedActions(Map<String, Action> questionedActions) {
		this.questionedActions = questionedActions;
	}
	
	
	
	@Scalar
	String usage;

	/**
	 * <p>Getter for the field <code>usage</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * <p>Setter for the field <code>usage</code>.</p>
	 *
	 * @param usage a {@link java.lang.String} object.
	 */
	public void setUsage(String usage) {
		this.usage = usage;
	}
}
