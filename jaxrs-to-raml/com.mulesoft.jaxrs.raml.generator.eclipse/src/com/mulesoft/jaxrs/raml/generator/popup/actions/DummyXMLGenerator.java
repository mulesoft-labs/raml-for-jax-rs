package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.ByteArrayOutputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.ui.internal.wizards.NewXMLGenerator;

@SuppressWarnings("restriction")
public class DummyXMLGenerator {

	private static String getRootElementName(NewXMLGenerator generator) {
		if (generator == null) {
			return null;
		}
		CMDocument cmDocument = generator.getCMDocument();
		if (cmDocument == null) {
			return null;
		}
		CMNamedNodeMap nameNodeMap = cmDocument.getElements();

		if (nameNodeMap == null) {
			return null;
		}

		for (int i = 0; i < nameNodeMap.getLength(); i++) {
			CMElementDeclaration cmElementDeclaration = (CMElementDeclaration) nameNodeMap
					.item(i);
			Object value = cmElementDeclaration.getProperty("Abstract"); //$NON-NLS-1$
			if (value != Boolean.TRUE) {
				return cmElementDeclaration.getElementName();
			}
		}

		return null;
	}

	String generateDummyXmlFor(String uri) {


		SchemaCdataHandler cdataHandler = new SchemaCdataHandler(uri);
		try {
			String enhancedUri = cdataHandler.getEnhancedSchemaURI();
			if (enhancedUri != null) {
				uri = enhancedUri;
			}

			String[] errorInfo = new String[2];

			IPath sl = new Path("");

			CMDocument cmDocument = ContentModelManager.getInstance()
					.createCMDocument(uri, null);

			NewXMLGenerator generator = new NewXMLGenerator(uri, cmDocument);

			String rootElementName = getRootElementName(generator);
			if (rootElementName == null) {
				return "NONE1";
			}
			generator.setRootElementName(rootElementName);

			try {

				CMNamedNodeMap nameNodeMap = generator.getCMDocument()
						.getElements();

				ByteArrayOutputStream result = generator.createXMLDocument(
						"testFile.xml", null); //$NON-NLS-1$

				return cdataHandler.enhanceGeneratedXML(result.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "NONE2";
		} finally {
			cdataHandler.cleanup();
		}
	}
}