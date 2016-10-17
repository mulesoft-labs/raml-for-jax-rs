package org.aml.typesystem.java;

import org.aml.typesystem.BuiltIns;

public class BuildinsBuilderFix extends BuiltinsBuilder{
	
	static{
		BuiltinsBuilder bld=BuiltinsBuilder.getInstance();
		bld.bldrs.put(Integer.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, "int32"));
		bld.bldrs.put(Long.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, "int64"));
		bld.bldrs.put(Short.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, "int16"));
		bld.bldrs.put(Byte.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, "int8"));
		bld.bldrs.put(Double.class.getName(), new SimpleBuilder(BuiltIns.NUMBER, false, double.class.getName()));
		bld.bldrs.put(Float.class.getName(), new SimpleBuilder(BuiltIns.NUMBER, false, float.class.getName()));
	}
}
