package org.raml.emitter;

import java.lang.reflect.Field;

import org.raml.model.Resource;
import org.raml.model.TemplateUse;

public class TypeDumper implements IRAMLFieldDumper{

	@Override
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		Resource t=(Resource) pojo;
		TemplateUse typeModelT = t.getTypeModelT();
		if (typeModelT!=null){
			dump.append(emitter.indent(depth));
			dump.append("type: ");
			
			if (typeModelT.getParameters().isEmpty()){
				dump.append(typeModelT.getKey());
			}
			else{
				dump.append("{ ");
				dump.append(typeModelT);
				dump.append(" }");
			}
			dump.append("\n");
			return;
		}
	}

}
