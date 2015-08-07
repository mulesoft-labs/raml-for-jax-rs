package org.raml.schema.model;

import java.util.HashMap;

public class DefaultValueFactory {
	
	public static final int DEFAULT_INTEGER_VALUE = 123;
	
	public static final double DEFAULT_DOUBLE_VALUE = 123.456;
	
	public static final float DEFAULT_FLOAT_VALUE = 123.456F;
	
	public static final char DEFAULT_CHARACTER_VALUE = 'a';
	
	public static final String DEFAULT_STRING_VALUE = "str1234";
	
	public static final boolean DEFAULT_BOOLEAN_VALUE = true;
	
	private static HashMap<SimpleType,Object> defaultvalueMap = new HashMap<SimpleType, Object>();
	static {
		defaultvalueMap.put(SimpleType.INTEGER,   DefaultValueFactory.DEFAULT_INTEGER_VALUE);
		defaultvalueMap.put(SimpleType.LONG,      DefaultValueFactory.DEFAULT_INTEGER_VALUE);
		defaultvalueMap.put(SimpleType.SHORT,     DefaultValueFactory.DEFAULT_INTEGER_VALUE);
		defaultvalueMap.put(SimpleType.BYTE,      DefaultValueFactory.DEFAULT_INTEGER_VALUE);
		defaultvalueMap.put(SimpleType.DOUBLE,    DefaultValueFactory.DEFAULT_DOUBLE_VALUE);
		defaultvalueMap.put(SimpleType.FLOAT,     DefaultValueFactory.DEFAULT_FLOAT_VALUE);
		defaultvalueMap.put(SimpleType.BOOLEAN,   DefaultValueFactory.DEFAULT_BOOLEAN_VALUE);
		defaultvalueMap.put(SimpleType.CHARACTER, DefaultValueFactory.DEFAULT_CHARACTER_VALUE);
		defaultvalueMap.put(SimpleType.STRING,    DefaultValueFactory.DEFAULT_STRING_VALUE);
	}
	
	public static Object getDefaultValue(ISchemaProperty prop){
		
		String propValue = prop.getDefaultValue();
		if(propValue!=null){
			return propValue;
		}
		
		if(prop.isGeneric()){
			return "Some " + prop.getName() + " value";
		}
		ISchemaType type = prop.getType();
		return getDefaultValue(type);
	}

	public static Object getDefaultValue(ISchemaType type){
		
		JAXBClassMapping mapping = type.getMapping();
		if(mapping!=null){
			return mapping.getExample();
		}
		
		if(type.isSimple()){
			if(type instanceof SimpleType){
				return defaultvalueMap.get((SimpleType)type);
			}			
		}
		return "some " + type.getName() + " value";
	}

}
