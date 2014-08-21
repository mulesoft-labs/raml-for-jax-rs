package org.raml.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.builder.QuestionableActionTypeHandler;

public class ResourceType extends Resource{

	@Mapping(handler = QuestionableActionTypeHandler.class, implicit = true)
    private Map<String, Action> questionedActions = new LinkedHashMap<String, Action>();

    public Map<String, Action> getQuestionedActions() {
		return questionedActions;
	}

	public void setQuestionedActions(Map<String, Action> questionedActions) {
		this.questionedActions = questionedActions;
	}
	
	
	
	@Scalar
	String usage;

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}
}
