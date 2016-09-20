package org.raml.jaxrs.codegen.maven;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;



public class RamlJaxrsCodegenMojoTest  {

	private RamlJaxrsCodegenMojo mojo;

	@Before
	public void init() throws Exception {
		mojo = new RamlJaxrsCodegenMojo();

	}



	@Test
	public void testcomputeSubPackageNameUseHierarchy() throws Exception {
		mojo.setBasePackageName("");
		mojo.setSourceDirectory(new File("src/raml/"));
		mojo.setUseSourceHiearchyInPackageName(true);
		assertEquals(".v1", mojo.computeSubPackageName(new File("src/raml/v1/widgets.raml")));
		assertEquals(".v2", mojo.computeSubPackageName(new File("src/raml/v2/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("src/raml/widgets.raml")));
		assertEquals(".v1", mojo.computeSubPackageName(new File("src/raml/v1/widgets.raml")));
		assertEquals(".productsummary", mojo.computeSubPackageName(new File("src/raml/product-summary/widgets.raml")));
	}
	
	@Test
	public void testcomputeSubPackageNameNoHierarchy() throws Exception {
		mojo.setBasePackageName("");
		mojo.setSourceDirectory(new File("/src/raml/"));
		mojo.setUseSourceHiearchyInPackageName(false);
		assertEquals("", mojo.computeSubPackageName(new File("/src/raml/v1/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("/src/raml/v2/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("/src/raml/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("src/raml/product-summary/widgets.raml")));
	}
	
	@Test
	public void testcomputeSubPackageNameNoSourceDirectory() throws Exception {
		mojo.setBasePackageName("");
		mojo.setUseSourceHiearchyInPackageName(true);
		mojo.setSourceDirectory(null);
		assertEquals("", mojo.computeSubPackageName(new File("/src/raml/v1/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("/src/raml/v2/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("/src/raml/widgets.raml")));
		assertEquals("", mojo.computeSubPackageName(new File("src/raml/product-summary/widgets.raml")));
	}

}
