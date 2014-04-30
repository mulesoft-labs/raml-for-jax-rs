/*
 * Copyright 2014, Genuitec, LLC
 * All Rights Reserved.
 */
package org.dynvocation.lib.xsd4j;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

public class XSDUtil {
	private XSDSchema testschema;
	private HashMap<?, ?> namespaces;

	private boolean debugging;

	public void setdebugging(boolean debugging) {
		this.debugging = debugging;
	}

	public boolean instantiate(String xsdfile, QName message, String outputFileName) {
		boolean ret;

		ret = parser(xsdfile);

		if (ret) {
			String result = instantiator(this.testschema, this.namespaces, message);
			try {
				PrintWriter writer = new PrintWriter(outputFileName);
				writer.print(result);
				writer.close();
			} catch (FileNotFoundException e) {
				System.err.println("File not found: " + outputFileName); //$NON-NLS-1$
			}
		}

		return ret;
	}

	public boolean parser(String xsdfile) {
		XSDParser parser = new XSDParser();

		this.testschema = parser.parseSchemaFile(xsdfile,
				XSDParser.PARSER_TREE);

		if (this.testschema == null) {
			System.err.println("-- parser failed!"); //$NON-NLS-1$
			System.err.println(parser.getDebug());
			return false;
		} else if (this.debugging) {
			System.out.println("-- parser successful!"); //$NON-NLS-1$
			System.out.println(parser.getDebug());
		}

		this.namespaces = parser.getNamespaces();

		return true;
	}

	private String instantiator(XSDSchema xsdschema, HashMap<?, ?> namespaces,
			QName message) {
		if (xsdschema.getLevel() < XSDParser.PARSER_TREE) {
			if (this.debugging) {
				System.out.println("-- must advance to tree level first!"); //$NON-NLS-1$
			}

			XSDTransformer xsdtransformer = new XSDTransformer();
			boolean ret = xsdtransformer.augment(xsdschema,
					XSDParser.PARSER_TREE);
			if (!ret) {
				System.err
						.println("-- transformation (for instantiation) failed!"); //$NON-NLS-1$
				System.err.println(xsdtransformer.getDebug());
				return null;
			} else if (this.debugging) {
				System.out
						.println("-- transformation (for instantiation) successful!"); //$NON-NLS-1$
				System.out.println(xsdtransformer.getDebug());
			}
		}

		XSDInstantiator xsdinstantiator = new XSDInstantiator();
		xsdinstantiator.declareNamespaces(namespaces);
		Document doc;
		if (message == null) {
			doc = xsdinstantiator.createWithRoot(xsdschema);
		} else {
			doc = xsdinstantiator.createelement(xsdschema, message);
		}

		if (doc == null) {
			System.err.println("-- instantiation failed!"); //$NON-NLS-1$
			System.err.println(xsdinstantiator.getDebug());
			return null;
		} else if (this.debugging) {
			System.out.println("-- instantiation successful!"); //$NON-NLS-1$
			System.out.println(xsdinstantiator.getDebug());
		}

		XSDDumper xsddumper = new XSDDumper();
		xsddumper.declareNamespaces(namespaces);
		String s = xsddumper.dumpXML(doc.getDocumentElement());
		return s;
	}
}
