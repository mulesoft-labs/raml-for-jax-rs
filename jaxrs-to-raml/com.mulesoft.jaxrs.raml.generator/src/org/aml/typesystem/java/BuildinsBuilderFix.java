package org.aml.typesystem.java;

import org.aml.typesystem.BuiltIns;

public class BuildinsBuilderFix extends BuiltinsBuilder{
	
	static{
		BuiltinsBuilder bld=BuiltinsBuilder.getInstance();
		bld.bldrs.put(Integer.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, int.class.getName()));
		bld.bldrs.put(Long.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, long.class.getName()));
		bld.bldrs.put(Short.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, short.class.getName()));
		bld.bldrs.put(Byte.class.getName(), new SimpleBuilder(BuiltIns.INTEGER, false, byte.class.getName()));
		bld.bldrs.put(Double.class.getName(), new SimpleBuilder(BuiltIns.NUMBER, false, double.class.getName()));
		bld.bldrs.put(Float.class.getName(), new SimpleBuilder(BuiltIns.NUMBER, false, float.class.getName()));
	}
}
