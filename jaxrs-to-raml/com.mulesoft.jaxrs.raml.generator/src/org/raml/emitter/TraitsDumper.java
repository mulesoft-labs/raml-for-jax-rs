package org.raml.emitter;

import java.lang.reflect.Field;
import java.util.List;

import org.raml.model.Action;
import org.raml.model.Resource;
import org.raml.model.TemplateUse;

/**
 * <p>TraitsDumper class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TraitsDumper implements IRAMLFieldDumper {

	
	/** {@inheritDoc} */
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		List<TemplateUse> resourceTypeMap = null;
		if (pojo instanceof Action) {
			Action v = (Action) pojo;
			resourceTypeMap = (List<TemplateUse>) v.getIsModel();
		}
		if (pojo instanceof Resource) {
			Resource v = (Resource) pojo;
			resourceTypeMap = (List<TemplateUse>) v.getIsModel();
		}
		if (resourceTypeMap.isEmpty()) {
			return;
		}
		dump.append(emitter.indent(depth));
		dump.append("is: ");
		if (false) {
			dump.append(resourceTypeMap.iterator().next());
			return;
		} else {
			int a = 0;
			dump.append("[ ");
			for (TemplateUse t : resourceTypeMap) {
				dump.append(t);
				a++;
				if (a < resourceTypeMap.size()) {
					dump.append(" , ");
				}
			}
			dump.append(" ]");
		}
		dump.append("\n");

	}

}
