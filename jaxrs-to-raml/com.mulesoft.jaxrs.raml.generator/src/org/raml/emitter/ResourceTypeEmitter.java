package org.raml.emitter;

import java.lang.reflect.Field;
import java.util.Map;

import org.raml.model.Raml2;
import org.raml.model.Resource;
import org.raml.model.ResourceType;

/**
 * <p>ResourceTypeEmitter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ResourceTypeEmitter implements IRAMLFieldDumper {

	
	/** {@inheritDoc} */
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		if (pojo instanceof Raml2){
			
		
		Raml2 v = (Raml2) pojo;
		Map<String, ResourceType> resourceTypeMap = v.getResourceTypesModel();
		if (resourceTypeMap.isEmpty()){
			return;
		}
		dump.append("resourceTypes:\n");
		if (emitter.isSeparated) {
			
			for (String q : resourceTypeMap.keySet()) {
				dump.append(emitter.indent(depth + 1));
				dump.append("- ");
				dump.append(q);
				dump.append(": ");
				dump.append("!include");
				dump.append(' ');
				dump.append("resourceTypes/");
				dump.append(q);
				dump.append(".raml");
				dump.append("\n");
				StringBuilder content = new StringBuilder();
				emitter.dumpPojo(content, 0, resourceTypeMap.get(q));
				if (emitter.writer != null) {
					emitter.writer.write("resourceTypes/"+q+".raml",content.toString());
				}
			}
		} else {
			dump.append(emitter.indent(depth + 1));
			emitter.dumpMapInSeq(dump, depth + 1, Resource.class,
					resourceTypeMap, false, true);
		}
		}
		else{
//			dump.append(emitter.indent(depth + 1));
//			emitter.dumpMapInSeq(dump, depth + 1, Resource.class,
//					resourceTypeMap, false, true);
		}
			
	}

}
