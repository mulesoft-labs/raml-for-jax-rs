/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.emitter;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.raml.parser.utils.ReflectionUtils.isEnum;
import static org.raml.parser.utils.ReflectionUtils.isPojo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.raml.model.DocumentationItem;
import org.raml.model.ParamType;
import org.raml.model.Protocol;
import org.raml.model.Raml;
import org.raml.model.SecurityReference;
import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.utils.ReflectionUtils;

public class RamlEmitterV2 {

	public static final String VERSION = "#%RAML 0.8\n";
	private static final String INDENTATION = "  ";
	private static final String YAML_SEQ = "- ";
	private static final String YAML_SEQ_START = "[";
	private static final String YAML_SEQ_END = "]";
	private static final String YAML_SEQ_SEP = ", ";
	private static final String YAML_MAP_SEP = ": ";
	private Field currentField;
	
	protected boolean isSeparated;
	protected IRamlHierarchyTarget writer;
	private boolean escape;
	
	public RamlEmitterV2(){
		isSeparated=true;
	}
	
	public void dump(IRamlHierarchyTarget writer,Raml r){
		isSeparated=true;
		this.writer=writer;
		String dump = dump(r);
		writer.writeRoot(dump);
	}

	public String dump(Raml raml) {
		StringBuilder dump = new StringBuilder(VERSION);
		int depth = 0;
		dumpPojo(dump, depth, raml);
		return dump.toString();
	}

	public void dumpPojo(StringBuilder dump, int depth, Object pojo) {
		
		final List<Field> declaredFields = ReflectionUtils
				.getInheritedFields(pojo.getClass());
		pojo.getClass();
		for (Field declaredField : declaredFields) {
			declaredField.setAccessible(true);
			Scalar scalar = declaredField.getAnnotation(Scalar.class);
			Mapping mapping = declaredField.getAnnotation(Mapping.class);
			Sequence sequence = declaredField.getAnnotation(Sequence.class);
			Dumper dumper = declaredField.getAnnotation(Dumper.class);
			if (dumper!=null){
				try {
					IRAMLFieldDumper newInstance = dumper.value().newInstance();
					newInstance.dumpField(dump,depth,declaredField,pojo, this);
					continue;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			if (scalar != null) {
				String includeField = scalar.includeField();
				dumpScalarField(dump, depth, declaredField, pojo,includeField);
			} else if (mapping != null) {
				boolean inlineLists = false;
				dumpMappingField(dump, depth, declaredField,
						mapping.implicit(), pojo, inlineLists);
			} else if (sequence != null) {
				dumpSequenceField(dump, depth, declaredField, pojo);
			}
		}
	}

	private Object getFieldValue(Field field, Object pojo) {
		try {
			return field.get(pojo);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public void dumpSequenceField(StringBuilder dump, int depth, Field field,
			Object pojo) {
		if (!List.class.isAssignableFrom(field.getType())) {
			throw new RuntimeException("Only List can be sequence.");
		}
		currentField=field;
		
		List seq = (List) getFieldValue(field, pojo);
		if (seq == null || seq.size() == 0) {
			return;
		}

		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			Type itemType = pType.getActualTypeArguments()[0];
			dump.append(indent(depth)).append(alias(field))
					.append(YAML_MAP_SEP);
			dumpSequenceItems(dump, depth, seq, itemType, false);
		}
	}

	void dumpSequenceItems(StringBuilder dump, int depth, List<?> seq,
			Type itemType, boolean inlineSeq) {
		if (itemType instanceof ParameterizedType) {
			generateSequenceOfMaps(dump, depth + 1, seq,
					(ParameterizedType) itemType);
			return;
		}
		if (customSequenceHandled(dump, depth + 1, seq, itemType)) {
			return;
		}
		if (isPojo((Class<?>) itemType)) {
			dump.append("\n");
			if (currentField.getName().equals("documentation")){
				for (Object item : seq) {
					DocumentationItem it=(DocumentationItem) item;
					dump.append(indent(depth + 1)).append(YAML_SEQ).append("title: ").append(it.getTitle()).append("\n");
					if (isSeparated){
					String origin = it.getOrigin();
					if (origin==null){
						origin="docs/"+it.getTitle().toLowerCase()+".md";
					}						
					dump.append(indent(depth + 2)).append("content: !include ").append(origin).append("\n");
					if (writer!=null){
						writer.write(origin, it.getContent());
					}
					}
					else{
						dump.append(indent(depth + 2)).append("content: ").append(sanitizeScalarValue(depth+2, it.getContent(), false)).append("\n");
					}
				
					//dumpPojo(dump, depth + 2, item);
				}
				return;
			}
			for (Object item : seq) {
				dump.append(indent(depth + 1)).append(YAML_SEQ).append("\n");
				dumpPojo(dump, depth + 2, item);
			}
		} else {
			if (seq.size()>2&&!currentField.getName().equals("is")) {
				dump.append("\n");
				for (Object item : seq) {
					dump.append(indent(depth + 1)).append(YAML_SEQ).append(sanitizeScalarValue(0, item, false)).append("\n");					
				}
			}
			else{
				generateInlineSequence(dump, seq, inlineSeq);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean customSequenceHandled(StringBuilder dump, int depth,
			List seq, Type itemType) {
		if ((itemType instanceof Class<?>)
				&& SecurityReference.class
						.isAssignableFrom((Class<?>) itemType)) {
			handleSecurityReference(dump, depth, seq);
		} else if ((itemType instanceof Class<?>)
				&& AbstractParam.class.isAssignableFrom((Class<?>) itemType)
				&& seq.size() == 1) {
			handleSingleParameterAsNoSeq(dump, depth, seq);
		} else {
			return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	private void handleSingleParameterAsNoSeq(StringBuilder dump, int depth,
			List seq) {
		dump.append("\n");
		dumpPojo(dump, depth, seq.get(0));
	}

	@SuppressWarnings("rawtypes")
	private void handleSecurityReference(StringBuilder dump, int depth, List seq) {
		ArrayList<String>sm=new ArrayList<String>();
		for (SecurityReference r:(List<SecurityReference>)seq){
			if (r.getParameters().isEmpty()){
				sm.add(r.getName());
			}
			else{
				sm=null;
				break;
			}
		}
		if (sm!=null){
			generateInlineSequence(dump, sm, false);
			return;
		}
		dump.append("\n");		
		for (Object item : seq) {
			dump.append(indent(depth)).append(YAML_SEQ);
			dump.append(
					((SecurityReference) item).getName());
			if (((SecurityReference) item).getParameters().size() > 0) {
				dump.append(YAML_MAP_SEP).append("\n");
				dumpMap(dump, depth + 2, String.class,
						((SecurityReference) item).getParameters(), false, false);
			} else {
				dump.append("\n");
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void generateSequenceOfMaps(StringBuilder dump, int depth,
			List seq, ParameterizedType itemType) {
		Type rawType = itemType.getRawType();
		if (rawType instanceof Class
				&& Map.class.isAssignableFrom((Class<?>) rawType)) {
			Type valueType = itemType.getActualTypeArguments()[1];
			if (valueType instanceof Class) {
				dump.append("\n");//TODO REVIEW
				for (Object item : seq) {
					dump.append(indent(depth)).append(YAML_SEQ);
					dumpMap(dump, depth + 1, valueType, (Map) item, false, true);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void generateInlineSequence(StringBuilder dump, List seq,
			boolean inlineSeq) {
		if (inlineSeq) {
			Object item = seq.get(0);
			dump.append(sanitizeScalarValue(0, item, false)).append("\n");
			return;
		}
		dump.append(YAML_SEQ_START);
		for (int i = 0; i < seq.size(); i++) {
			Object item = seq.get(i);
			if(i==0){
				dump.append(' ');
			}
			dump.append(sanitizeScalarValue(0, item, false));
			dump.append(' ');
			if (i < seq.size() - 1) {
				dump.append(YAML_SEQ_SEP);
			}
		}
		dump.append(YAML_SEQ_END).append("\n");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void dumpMappingField(StringBuilder dump, int depth, Field field,
			boolean implicit, Object pojo, boolean inlineLists) {
		if (!Map.class.isAssignableFrom(field.getType())) {
			throw new RuntimeException("invalid type");
		}
		
		Map value = (Map) getFieldValue(field, pojo);
		MapFilter annotation = field.getAnnotation(MapFilter.class);
		if (annotation!=null){
			try{
			IFilter newInstance = annotation.value().newInstance();
			LinkedHashMap q=new LinkedHashMap();
			for (Object a:value.keySet()){
				Object object = value.get(a);
				if (newInstance.accept(object)){
					q.put(a, object);
				}
			}
			value=q;
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (value == null || value.isEmpty()) {
			return;
		}
		boolean isSettings = false;
		if (field.getName().equals("settings")) {
			isSettings = true;
		}

		if (!implicit) {
			dump.append(indent(depth)).append(alias(field))
					.append(YAML_MAP_SEP).append("\n");
			depth++;
		}

		ParameterizedType pType = (ParameterizedType) field.getGenericType();
		Type valueType = pType.getActualTypeArguments()[1];		
		dumpMap(dump, depth, valueType, value, isSettings, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void dumpMap(StringBuilder dump, int depth, Type valueType,
			Map value, boolean isSettings,boolean newLine) {
		Type listType = null;
		if (valueType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) valueType).getRawType();
			if (rawType instanceof Class
					&& List.class.isAssignableFrom((Class<?>) rawType)) {
				listType = ((ParameterizedType) valueType)
						.getActualTypeArguments()[0];
			}
		}
		
		int k=newLine?0:depth;
		// body
		for (Map.Entry entry : (Set<Map.Entry>) value.entrySet()) {
			dump.append(indent(k)).append(
					sanitizeScalarValue(depth, entry.getKey(), false));
			k=depth;
			dump.append(YAML_MAP_SEP);

			if (listType != null) {
				if (isSettings) {
					boolean inlineSeq = false;
					String string = entry.getKey().toString();
					if (string.equals("authorizationUri")||string.equals("requestTokenUri")||string.equals("tokenCredentialsUri")) {
						inlineSeq = true;
					} else if (string
							.equals("accessTokenUri")) {
						inlineSeq = true;
					}					
					dumpSequenceItems(dump, depth, (List) entry.getValue(),
							listType, inlineSeq);
				} else {
					dumpSequenceItems(dump, depth, (List) entry.getValue(),
							listType, false);
				}
			} else if (isPojo((Class<?>) valueType)) {
				dump.append("\n");
				dumpPojo(dump, depth + 1, entry.getValue());
			} else // scalar
			{
				dump.append(sanitizeScalarValue(depth + 1, entry.getValue(), true))
						.append("\n");
			}
		}

	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void dumpMapInSeq(StringBuilder dump, int depth, Type valueType,
			Map value, boolean isSettings,boolean newLine) {
		Type listType = null;
		if (valueType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) valueType).getRawType();
			if (rawType instanceof Class
					&& List.class.isAssignableFrom((Class<?>) rawType)) {
				listType = ((ParameterizedType) valueType)
						.getActualTypeArguments()[0];
			}
		}
		
		int k=newLine?0:depth;
		// body
		for (Map.Entry entry : (Set<Map.Entry>) value.entrySet()) {
			dump.append(indent(k)).append("- ").append(
					sanitizeScalarValue(depth, entry.getKey(), false));
			k=depth;
			
			dump.append(YAML_MAP_SEP);

			if (listType != null) {
				if (isSettings) {
					boolean inlineSeq = false;
					if (entry.getKey().toString().equals("authorizationUri")) {
						inlineSeq = true;
					} else if (entry.getKey().toString()
							.equals("accessTokenUri")) {
						inlineSeq = true;
					}
					dumpSequenceItems(dump, depth+2, (List) entry.getValue(),
							listType, inlineSeq);
				} else {
					dumpSequenceItems(dump, depth+2, (List) entry.getValue(),
							listType, false);
				}
			} else if (isPojo((Class<?>) valueType)) {
				dump.append("\n");
				dumpPojo(dump, depth + 2, entry.getValue());
			} else // scalar
			{
				dump.append(sanitizeScalarValue(depth + 2, entry.getValue(), true))
						.append("\n");
			}
		}

	}

	private void dumpScalarField(StringBuilder dump, int depth, Field field,
			Object pojo, String includeField) {
		try {
			currentField=field;
			Object value = field.get(pojo);
			if (field.getName().equals("content")){
				System.out.println("a");
			}
			if (value == ParamType.STRING) {
				return;
			}
			if (field.getName().equals("required")) {
				if (value != null && value.equals(false)) {
					return;
				}
			}
			if (field.getName().equals("repeat")) {
				if (value != null && value.equals(false)) {
					return;
				}
			}
			if (field.getName().equals("schema")) {
				value = adjustSchema(value);
			}
			if (field.getName().equals("content")) {
				value = adjustDocumentationContent(value);
			}
			if (field.getName().equals("example")) {
				value = adjustExample(value);
			}
			if (value == null) {
				return;
			}
			dump.append(indent(depth)).append(alias(field))
					.append(YAML_MAP_SEP);
			if (isPojo(value.getClass())) {
				dump.append("\n");
				dumpPojo(dump, depth + 1, value);
			} else {
				String sanitizeScalarValue = sanitizeScalarValue(depth, value, true);
				if (isSeparated&& includeField!=null&&includeField.length()>0){
					try {
						Field declaredField = field.getDeclaringClass().getDeclaredField(includeField);
						declaredField.setAccessible(true);
						Object object = declaredField.get(pojo);						
						if (object!=null&&object instanceof String){							
							dump.append("!include "+object.toString()).append("\n");
							if (writer!=null){
								writer.write(object.toString(), value.toString());
							}
							return;
						}
						
					} catch (NoSuchFieldException e) {
						throw new IllegalStateException();						
					} catch (SecurityException e) {
						throw new IllegalStateException();
					}
				}
				
				dump.append(sanitizeScalarValue).append("\n");
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private Object adjustSchema(Object value) {
//		if (value != null) {
//			value = "schema";
//		}
		return value;
	}

	private Object adjustDocumentationContent(Object value) {
//		if (value != null) {
//			value = "documentation";
//		}
		return value;
	}

	private Object adjustExample(Object value) {		
//		if (value != null) {
//			if (value.toString().length() > 30) {
//				value = "example";
//			}
//		}
		return value;
	}

	private String alias(Field field) {
		Scalar scalar = field.getAnnotation(Scalar.class);
		Mapping mapping = field.getAnnotation(Mapping.class);
		Sequence sequence = field.getAnnotation(Sequence.class);

		if (scalar != null && isNotEmpty(scalar.alias())) {
			return scalar.alias();
		} else if (mapping != null && isNotEmpty(mapping.alias())) {
			return mapping.alias();
		} else if (sequence != null && isNotEmpty(sequence.alias())) {
			return sequence.alias();
		}
		return field.getName();
	}

	private String sanitizeScalarValue(int depth, Object value, boolean isValue) {		
		Class<?> type = value.getClass();
		String result = handleCustomScalar(value);
		if (result != null) {
			return result;
		}
		if (isEnum(type)) {
			result = String.valueOf(value).toLowerCase();
		} else if (String.class.isAssignableFrom(type)) {
			String text = (String) value;
			text=text.replace((CharSequence)"\t", "  ");
			if (text.contains("\n")||(text.contains("\"")&&text.contains("'"))) {
				
				result = blockFormat(depth, text);
			} else {
				result = inlineFormat(depth, text, isValue);
			}
		} else {
			result = String.valueOf(value);
		}
		return result;
	}

	private String handleCustomScalar(Object value) {
		if (value instanceof Protocol) {
			return String.valueOf(value);
		}
		return null;
	}

	private String inlineFormat(int depth, String text, boolean isValue) {
		boolean isIdentifier = true;
		if (text.length()==0){
			return "\"" + "\""; 
		}
		if (text.startsWith("!include")){
			return "\"" +text+ "\""; 
		}
		if (currentField.getName().equals("schemas")){
			
			return text;
		}
		
		if (currentField.getName().contains("relative")){
			return text;
		}
		if (text.contains("\"")){
			
			return '\''+text+'\'';
		}
		if (text.contains("*")){
			return "\"" + text + "\"";
		}
		if (text.contains("{")&&escape){
			return "\"" + text + "\"";
		}
		for (int a = 0; a < text.length(); a++) {
			char c = text.charAt(a);
			if (a==0&&c=='{'&&text.endsWith("}")){
				if (currentField.getName().equals("type")||currentField.getName().equals("is")){
					return text;
				}
			}
			if (!isValue && Character.isWhitespace(c)) {
				isIdentifier = false;
				break;
			}
			if (c == ':') {
				
				if(a-"https".length()>=0){
					if(!text.startsWith("https:",a-"https".length())){
						isIdentifier = false;
						break;
					}
				}
				else if(a-"http".length()>=0){
					if(!text.startsWith("http:",a-"http".length())){
						isIdentifier = false;
						break;
					}
				}
				else{
					isIdentifier = false;
					break;
				}
			}
			if (c == '"') {
				isIdentifier = false;
				break;
			}
//			if (c=='{'){
//				isIdentifier=false;
//			}
			if (c == '\'') {
				isIdentifier = false;
				break;
			}
		}
		if (isIdentifier) {
			return text;
		}
		
		if (!text.contains("\"")) {
			return "\"" + text + "\"";
		}
		if (!text.contains("'")) {
			return "'" + text + "'";
		}
		return blockFormat(depth, text);
	}

	private String blockFormat(int depth, String text) {
		StringBuilder block = new StringBuilder("|\n");
		String[] lines = text.split("\n");
		for (String line : lines) {
			block.append(indent(depth + 1)).append(line).append("\n");
		}
		return block.substring(0, block.length() - 1);
	}

	public String indent(int depth) {
		return StringUtils.repeat(INDENTATION, depth);
	}

	public void setSingle(boolean b) {
		this.isSeparated=false;
	}

	public void setEscapeLiterals(boolean b) {
		this.escape=b;
	}
}