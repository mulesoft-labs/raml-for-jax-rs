package org.raml.jaxrs.codegen.spoon;

import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCatch;

public class JaxrsSpoonProcessor extends AbstractProcessor<CtCatch> {

	public List<CtCatch> emptyCatchs = new ArrayList<CtCatch>();

	public void process(CtCatch element) {
		
		
		System.out.println("\n\n" + element.getSignature() + " !!!");
		
	}

}
