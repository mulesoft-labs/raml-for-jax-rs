package com.mulesoft.jaxrs.raml.jsonschema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <p>JsonSchemaNode class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JsonSchemaNode implements ISchemaNode{
	
	/**
	 * <p>Constructor for JsonSchemaNode.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param object a {@link org.codehaus.jettison.json.JSONObject} object.
	 * @param parent a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public JsonSchemaNode(String name, JSONObject object, JsonSchemaNode parent) {
		super();
		this.name = name != null ? name.trim() : "";
		this.object = object;
		this.parent = parent;
	}

	JsonSchemaNode parent;

	private String name;	
	
	private JSONObject object;
	
	private LinkedHashMap<String,JsonSchemaNode> properties=null;
	
	private LinkedHashSet<JsonSchemaNode> arrayItems=null;
	
	private String type = null;
	
	private Boolean required = null;
	
	/**
	 * <p>Getter for the field <code>parent</code>.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public JsonSchemaNode getParent() {
		return parent;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		if(this.name==null){
			if(name==null)
				return;
		}
		else if(this.name.equals(name)){
			return;
		}
		
		this.name = name;
		fireChanges();
	}

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getType(){
		
		if(type != null)
			return type;
		
		if(object.has("type")){
			try {
				type = object.getString("type");			
			} catch (JSONException e) {			
			}
		}
		else{
			type="string";
		}
		return type;		
	}
	
	/**
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type){
		
		if(this.type==null){
			if(type==null)
				return;
		}
		else if(this.type.equals(type)){
			return;
		}		
		setTypeSilent(type);		
		fireChanges();
	}

	private void setTypeSilent(String type) {
		this.type = type;
		try {
			object.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>getChildren.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<JsonSchemaNode> getChildren(){
		
		Collection<JsonSchemaNode> children = null;
		String type = getType();
		if("array".equals(type)) {
			children = getArrayItems();			
		}
		else if("object".equals(type)){
			children = getProperties();
		}		
		return children;				
	}
	
//	public Collection<ISchemaNode> getChildren(){
//		
//		ArrayList<ISchemaNode> children = new ArrayList<ISchemaNode>(
//			Arrays.asList( new TypeNode(), new RequiredNode() ) );
//		
//		String type_ = getType();
//		if("array".equals(type_)) {
//			children.add( new ArrayItemsNode() ) ;			
//		}
//		else if("object".equals(type_)){
//			children.add( new PropertiesNode() ) ;
//		}		
//		return children;				
//	}
	
	private Collection<JsonSchemaNode> getArrayItems() {
		
		if( arrayItems != null )
			return arrayItems;
		
		arrayItems = new LinkedHashSet<JsonSchemaNode>();
		reFillArrayItems();
		return arrayItems;
	}	

	/**
	 * <p>Getter for the field <code>properties</code>.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<JsonSchemaNode> getProperties(){
		if(properties != null)
			return properties.values();
		
		properties = new LinkedHashMap<String,JsonSchemaNode>();
		reFillProperties();
		return properties.values();
	}	
	
	/**
	 * <p>isRequired.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isRequired(){
		if(required!=null)
			return required;
		
		if(object.has("required")){
			try {
				required = object.getBoolean("required");			
			} catch (JSONException e) {
				required=false;
		    }
		}
		else{
			required=false;
		}
		return required;
	}
	
	/**
	 * <p>Setter for the field <code>required</code>.</p>
	 *
	 * @param required a boolean.
	 */
	public void setRequired(boolean required){
		
		if(this.required!=null&&required == this.required)
			return;
		
		setRequiredSilent(required);		
		fireChanges();
	}

	private void setRequiredSilent(boolean required) {
		this.required=required;
		try {
			object.put("required",required);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>setProperty.</p>
	 *
	 * @param property a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public void setProperty(JsonSchemaNode property){
		
		JSONObject propertiesObject = null;
		try {
			propertiesObject = object.getJSONObject("properties");			
		} catch (JSONException e) {
		}
		if(propertiesObject==null){
			propertiesObject = new JSONObject();
			try {
				object.put("properties", propertiesObject);
			} catch (JSONException e) {
			}
		}
		try {
			propertiesObject.put(property.getName(), property.object );
		} catch (JSONException e) {
		}
		if(properties==null)
			properties = new LinkedHashMap<String, JsonSchemaNode>();

		properties.put(property.getName(), property);
		fireChanges();
	}
	
	/**
	 * <p>addArrayItem.</p>
	 *
	 * @param item a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public void addArrayItem(JsonSchemaNode item){
		
		try {
			Object itemsObject = object.get("items");
			if(itemsObject instanceof JSONArray){
				JSONArray arr = (JSONArray) itemsObject;
				arr.put(item.object);
			}
			else if(itemsObject instanceof JSONObject)
			{
				JSONObject itemJsonObj = (JSONObject) itemsObject;
				if(itemJsonObj.keys().hasNext()){
					JSONArray arr = new JSONArray();
					arr.put(itemJsonObj);
					arr.put(item.object);
					object.put("items", arr );
				}
				else{
					object.put("items", item.object );
				}
			}
			else{
				object.put("items", item.object );			
			}						
		} catch (JSONException e) {
		}
		if( this.arrayItems == null ){
			this.arrayItems = new LinkedHashSet<JsonSchemaNode>();
		}
		arrayItems.add(item);
		try {
			object.put("items", item.object);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireChanges();
	}
	
	/**
	 * <p>removeProperty.</p>
	 *
	 * @param property a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public void removeProperty(JsonSchemaNode property){
		
		JSONObject propertiesObject = null;
		try {
			propertiesObject = object.getJSONObject("properties");			
		} catch (JSONException e) {
		}
		if(propertiesObject==null)
			return;
		
		propertiesObject.remove(property.getName());
		if(properties!=null)
			properties.remove(property.getName());
		
		fireChanges();
	}
	
	
	
	private void reFillProperties() {
		try {
			this.properties.clear();
			JSONObject propertiesObject = object.getJSONObject("properties");			
			
			for( Iterator<?> keys = propertiesObject.keys();keys.hasNext();){
				Object key = keys.next();
				if(key==null)
					continue;
				
				String propName = key.toString();
				JSONObject propObj = propertiesObject.getJSONObject(propName);
				JsonSchemaNode propNode = new JsonSchemaNode(propName, propObj, this);
				properties.put(propName, propNode);
			}			
		} catch (JSONException e) {
		}
	}
	
	private void reFillArrayItems() {
		try {
			this.arrayItems.clear();
			Object items = object.get("items");
			if(items instanceof JSONObject){
				JSONObject itemObj = (JSONObject) items;
				JsonSchemaNode propNode = new JsonSchemaNode("", itemObj, this);
				arrayItems.add(propNode);
			}
			else if(items instanceof JSONArray){
				JSONArray itemsArray = (JSONArray) items;		
				
				int l = itemsArray.length();
				for( int i = 0 ; i < l ; i++ ){
					Object itemObject = itemsArray.get(i);
					if(!(itemObject instanceof JSONObject))
						continue;
					
					JSONObject item = (JSONObject) itemObject;
					JsonSchemaNode propNode = new JsonSchemaNode("", item, this);
					arrayItems.add(propNode);
				}
			}
		} catch (JSONException e) {
		}
	}
	
	/**
	 * <p>getFormattedStringValue.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 * @throws org.codehaus.jettison.json.JSONException if any.
	 * @throws java.io.IOException if any.
	 */
	public String getFormattedStringValue() throws JSONException, IOException{
		String value = JsonUtils.transformObjectToStringAndFormat(object);
		return value;
	}
	
	/**
	 * <p>addChildNode.</p>
	 *
	 * @param node a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public void addChildNode(JsonSchemaNode node){
		if(type.equals("array")){
			this.addArrayItem(node);
		}
		else if(type.equals("object")){
			this.setProperty(node);
		}
	}
	
	

	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		String name_ = getName();		
		String type_ = getType();
		boolean required_ = isRequired();
		
		StringBuilder bld = new StringBuilder(name_);
		bld.append(" (");
		bld.append("type=").append(type_);

		if(required_){
			bld.append(", required=true") ;
		}
		bld.append(")");
		
		String result = bld.toString();
		return result;
	}
	
	public class TypeNode implements ISchemaNode{

		
		public Collection<? extends ISchemaNode> getChildren() {
			return null;
		}
		
		
		public String toString() {
			return "type: "+ getType();
		}		
	}
	
	public class RequiredNode implements ISchemaNode{

		
		public Collection<? extends ISchemaNode> getChildren() {
			return null;
		}
		
		
		public String toString() {
			return "required: "+ isRequired();
		}		
	}
	
	public class PropertiesNode implements ISchemaNode{

		
		public Collection<? extends ISchemaNode> getChildren() {
			return getProperties() ;
		}
		
		
		public String toString() {
			return "properties:";
		}		
	}
	
	public class ArrayItemsNode implements ISchemaNode{

		
		public Collection<? extends ISchemaNode> getChildren() {
			return getArrayItems() ;
		}
		
		
		public String toString() {
			return "items:";
		}		
	}

	
	private void fireChanges()
	{
			
	}

	/**
	 * <p>compare.</p>
	 *
	 * @param node1 a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 * @param node2 a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 * @return a boolean.
	 */
	public static boolean compare(JsonSchemaNode node1, JsonSchemaNode node2) {
		
		ArrayList<JsonSchemaNode> list1 = new ArrayList<JsonSchemaNode>() ;
		ArrayList<JsonSchemaNode> list2 = new ArrayList<JsonSchemaNode>() ;
		
		list1.add(node1);
		list2.add(node2);		
		
		for( int i = 0 ; i < list1.size() ; i++ ){
			JsonSchemaNode n1 = list1.get(i);
			JsonSchemaNode n2 = list2.get(i);
			
			if(!doCompare(n1, n2, list1, list2))
				return false;
		}		
		return true;
	}

	private static boolean doCompare(
			JsonSchemaNode node1, JsonSchemaNode node2,
			ArrayList<JsonSchemaNode> list1, ArrayList<JsonSchemaNode> list2)
	{		
		if(node1==null){
			return node2==null;
		}
		else if(node2==null){
			return false;
		}
		
		if( node1.isRequired() != node2.isRequired() )
			return false;
		
		if( !node1.getType().equals(node2.getType()) )
			return false;
		
		if( !node1.getName().equals(node2.getName()) )
			return false;
		
		if(node1.getType().toLowerCase().equals("object"))
		{
			Collection<JsonSchemaNode> props1 = node1.getProperties();
			Collection<JsonSchemaNode> props2 = node2.getProperties();
			if (!attachArrays(props1, props2, list1, list2) )
				return false;
		}
		else if(node2.getType().toLowerCase().equals("array")){
			Collection<JsonSchemaNode> items1 = node1.getArrayItems();
			Collection<JsonSchemaNode> items2 = node2.getArrayItems();
			if (!attachArrays(items1, items2, list1, list2) )
				return false;
		}
		return true;
	}

	private static boolean attachArrays(
			Collection<JsonSchemaNode> arr1,
			Collection<JsonSchemaNode> arr2,
			ArrayList<JsonSchemaNode> list1,
			ArrayList<JsonSchemaNode> list2)
	{
		int size = arr1.size();
		if(size != arr2.size())
			return false;
		
		Iterator<JsonSchemaNode> i1 = arr1.iterator();
		Iterator<JsonSchemaNode> i2 = arr2.iterator();
		for( int i = 0 ; i < size ; i++ ){
			
			JsonSchemaNode prop1 = i1.next();
			JsonSchemaNode prop2 = i2.next();
			list1.add(prop1);
			list2.add(prop2);
		}
		
		return true;
	}

	/**
	 * <p>update.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param type a {@link java.lang.String} object.
	 * @param required a boolean.
	 */
	public void update(String name, String type, boolean required) {
		
		boolean gotChange = false;
		if(this.name==null){
			gotChange |= name != null;
		}
		else{
			gotChange |= !this.name.equals(name);
		}		
		if(this.type==null){
			gotChange |= type != null;
		}
		else{
			gotChange = !this.name.equals(type);
		}
		if(this.required==null){
			gotChange = true;
		}
		else{
			gotChange |= this.required != required;
		}
		
		this.name = name;
		setTypeSilent(type);
		setRequiredSilent(required);
		
		if(!this.type.equals("object")){
			clearProperties();
		}
		if(!this.type.equals("array")){
			clearArrayItems();
		}
		
		if(gotChange)
			fireChanges();		
	}

	private void clearProperties() {

		object.remove("properties");
		this.properties=null;
	}

	private void clearArrayItems() {
		
		object.remove("items");
		this.arrayItems = null;
	}
}


