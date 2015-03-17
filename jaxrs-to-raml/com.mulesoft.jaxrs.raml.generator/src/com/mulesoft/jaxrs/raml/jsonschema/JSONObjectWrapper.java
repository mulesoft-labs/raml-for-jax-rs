package com.mulesoft.jaxrs.raml.jsonschema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <p>JSONObjectWrapper class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JSONObjectWrapper {
	
	
	private JSONObject object;
	
	private HashMap<String,JSONObjectWrapper> properties = new HashMap<String, JSONObjectWrapper>();
	
	private String type;

	/**
	 * <p>Constructor for JSONObjectWrapper.</p>
	 *
	 * @param object a {@link org.codehaus.jettison.json.JSONObject} object.
	 */
	public JSONObjectWrapper(JSONObject object) {
		super();
		this.object = object;
		
		try {
			this.type = object.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject props = null ;
		try{
			props = object.getJSONObject("properties");
		} catch (JSONException e) {
			props = null;
		}
		
		if( props != null ) {
			for( Iterator<?> iter = props.keys(); iter.hasNext() ; ){
				Object o = iter.next();
				String propName = o.toString();
				JSONObject property;
				try {
					property = props.getJSONObject(propName);
					JSONObjectWrapper child = new JSONObjectWrapper(property);
					properties.put(propName, child);
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}			
		}
	}
	
	/**
	 * <p>Getter for the field <code>object</code>.</p>
	 *
	 * @return a {@link org.codehaus.jettison.json.JSONObject} object.
	 */
	public JSONObject getObject() {
		return object;
	}
	
	

	
	/** {@inheritDoc} */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JSONObjectWrapper other = (JSONObjectWrapper) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		
		HashMap<String, JSONObjectWrapper> thisProperties = this.properties;
		Set<String> thisKeys = thisProperties.keySet();
		HashMap<String, JSONObjectWrapper> thatProperties = ((JSONObjectWrapper)other).properties;
		Set<String> otherKeys = thatProperties.keySet();
		if(!thisKeys.containsAll(otherKeys)){
			return false;
		}
		if(!otherKeys.containsAll(thisKeys)){
			return false;
		}
		for(String key : thisKeys){
			JSONObjectWrapper thisProp = thisProperties.get(key);
			JSONObjectWrapper thatProp = thatProperties.get(key);
			if(!thisProp.equals(thatProp))
				return false;
		}
		return true;
	}

	
	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
}
