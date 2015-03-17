package org.raml.emitter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.raml.model.Raml2;
import org.raml.model.SecurityScheme;

/**
 * <p>SecuritySchemeEmitter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SecuritySchemeEmitter implements IRAMLFieldDumper{

	
	/** {@inheritDoc} */
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		if (pojo instanceof Raml2){
		Raml2 v=(Raml2) pojo;
		List<Map<String, SecurityScheme>> resourceTypeMap = v.getSecuritySchemes();
		if (resourceTypeMap.isEmpty()){
			return;
		}
		
		if (emitter.isSeparated) {
			dump.append("securitySchemes:\n");
			for (Map<String,SecurityScheme> q : resourceTypeMap) {
				dump.append(emitter.indent(depth + 1));
				dump.append("- ");
				String name = q.keySet().iterator().next();
				dump.append(name);
				dump.append(": ");
				dump.append("!include");
				dump.append(' ');
				dump.append("securitySchemes/");
				dump.append(name);
				dump.append(".raml");
				dump.append("\n");
				StringBuilder content = new StringBuilder();
				emitter.dumpPojo(content, 0, q.values().iterator().next());
				if (emitter.writer != null) {
					emitter.writer.write("securitySchemes/"+name+".raml",content.toString());
				}
			}
		} else {
			dump.append(emitter.indent(depth));		
			emitter.dumpSequenceField(dump, depth, declaredField, pojo);
		}	
		}
		else{
			dump.append(emitter.indent(depth));		
			emitter.dumpSequenceField(dump, depth, declaredField, pojo);
		}
			
	}

}
