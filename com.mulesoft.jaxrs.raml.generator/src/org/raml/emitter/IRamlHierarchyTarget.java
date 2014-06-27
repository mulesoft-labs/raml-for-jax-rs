package org.raml.emitter;

public interface IRamlHierarchyTarget {

	void write(String path,String content);
	void writeRoot(String content);
	
}
