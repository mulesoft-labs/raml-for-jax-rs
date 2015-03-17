package org.raml.emitter;

import java.lang.reflect.Field;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.Raml2;
import org.raml.model.TraitModel;

/**
 * <p>TraitEmitter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TraitEmitter implements IRAMLFieldDumper{

	
	/** {@inheritDoc} */
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		if (pojo instanceof Raml2){
		Raml2 v=(Raml2) pojo;
		Map<String, TraitModel> resourceTypeMap = v.getTraitsModel();
		if (resourceTypeMap.isEmpty()){
			return;
		}
		dump.append("traits:\n");
		
		if (emitter.isSeparated) {
			for (String q : resourceTypeMap.keySet()) {
				dump.append(emitter.indent(depth + 1));
				dump.append("- ");
				dump.append(q);
				dump.append(": ");
				dump.append("!include");
				dump.append(' ');
				dump.append("traits/");
				dump.append(q);
				dump.append(".raml");
				dump.append("\n");
				StringBuilder content = new StringBuilder();
				emitter.dumpPojo(content, 0, resourceTypeMap.get(q));
				if (emitter.writer != null) {
					emitter.writer.write("traits/"+q+".raml",content.toString());
				}
			}
		} else {
		dump.append(emitter.indent(depth+1));		
		emitter.dumpMapInSeq(dump, depth+1, Action.class, resourceTypeMap, false, true);
		}
		}
	}
}
