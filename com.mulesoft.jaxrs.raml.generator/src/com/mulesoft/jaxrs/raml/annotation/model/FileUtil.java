/*
 * Copyright 2014, Genuitec, LLC
 * All Rights Reserved.
 */
package com.mulesoft.jaxrs.raml.annotation.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtil {

	public static String fileToString(File file) {
		String text;
		try {
			text = new Scanner(file).useDelimiter("\\A").next(); //$NON-NLS-1$
			return text;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
