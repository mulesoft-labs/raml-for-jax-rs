package org.raml.parser.builder;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.parser.resolver.ITransformHandler;

public class QuestionedActionTypeHandler implements ITransformHandler{

	public Object handle(Object value,Object parent){
		if (value.toString().endsWith("?")){
		Action c=(Action) parent;
		c.setQuestioned(true);
		return ActionType.valueOf(value.toString().substring(0,value.toString().length()-1).toUpperCase());
		}
		return value;
	}
}
