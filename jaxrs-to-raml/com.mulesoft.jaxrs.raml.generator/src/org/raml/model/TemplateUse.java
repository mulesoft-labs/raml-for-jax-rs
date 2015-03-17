package org.raml.model;

import java.util.LinkedHashMap;

/**
 * <p>TemplateUse class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TemplateUse {

	protected String key;
	protected LinkedHashMap<String, String>parameters=new LinkedHashMap<String, String>();
	/**
	 * <p>Constructor for TemplateUse.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 */
	public TemplateUse(String value) {
		this.key=value;
	}
	/**
	 * <p>Constructor for TemplateUse.</p>
	 */
	public TemplateUse() {
	}
	/**
	 * <p>Getter for the field <code>key</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getKey() {
		return key;
	}
	/**
	 * <p>Setter for the field <code>key</code>.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * <p>Getter for the field <code>parameters</code>.</p>
	 *
	 * @return a {@link java.util.LinkedHashMap} object.
	 */
	public LinkedHashMap<String, String> getParameters() {
		return parameters;
	}
	/**
	 * <p>Setter for the field <code>parameters</code>.</p>
	 *
	 * @param parameters a {@link java.util.LinkedHashMap} object.
	 */
	public void setParameters(LinkedHashMap<String, String> parameters) {
		this.parameters = parameters;
	}	
	
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		if (parameters.isEmpty()){
			return key;
		}
		else{
			StringBuilder bld=new StringBuilder();
			bld.append(key);
			bld.append(':');
			bld.append(' ');
			bld.append('{');
			int a=0;
			for (String s: parameters.keySet()){
				bld.append(' ');
				bld.append(s);
				bld.append(": ");
				String str = parameters.get(s);
				if (str.startsWith("!include")){
					str="\""+str+"\"";
				}
				bld.append(str);
				a++;
				if (a!=parameters.size()){
					bld.append(',');
				}
			}
			bld.append(' ');
			bld.append('}');
			return bld.toString();
		}	
	}
}
