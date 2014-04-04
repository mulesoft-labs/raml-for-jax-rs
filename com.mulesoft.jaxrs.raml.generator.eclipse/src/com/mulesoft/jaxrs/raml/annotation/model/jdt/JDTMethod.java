package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;

public class JDTMethod extends JDTAnnotatable implements IMethodModel {

	private static final String PARAM = "@param";
	private static final String RETURN = "@return";

	public JDTMethod(IMethod tm) {
		super(tm);
	}

	@Override
	public String getName() {
		return ((IMethod) tm).getElementName();
	}

	@Override
	public IParameterModel[] getParameters() {
		try {
			ILocalVariable[] parameters = ((IMethod) tm).getParameters();
			IParameterModel[] mm = new IParameterModel[parameters.length];
			int a = 0;
			for (ILocalVariable v : parameters) {

				mm[a++] = new JDTParameter(v);
			}
			return mm;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String getDocumentation() {
		try {
			IMethod iMethod = (IMethod) tm;
			ISourceRange javadocRange = iMethod.getJavadocRange();
			if (javadocRange != null) {
				String attachedJavadoc = iMethod
						.getCompilationUnit()
						.getSource()
						.substring(
								javadocRange.getOffset(),
								javadocRange.getOffset()
										+ javadocRange.getLength());
				attachedJavadoc = attachedJavadoc.substring(3,
						attachedJavadoc.length() - 2);
				StringReader rr = new StringReader(attachedJavadoc);
				BufferedReader mm = new BufferedReader(rr);
				StringBuilder bld = new StringBuilder();
				while (true) {
					try {
						String s = mm.readLine();
						if (s == null) {
							break;
						}
						int indexOf = s.indexOf('*');
						if (indexOf != -1) {
							s = s.substring(indexOf + 1);
						}
						s = s.trim();
						if (s.startsWith("@")) {
							continue;
						}
						bld.append(s);
						bld.append('\n');
					} catch (IOException e) {
						break;
					}
				}
				return bld.toString().trim();
			}
			return null;
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public IDocInfo getBasicDocInfo() {
		try {
			IMethod iMethod = (IMethod) tm;
			ISourceRange javadocRange = iMethod.getJavadocRange();
			if (javadocRange != null) {
				String attachedJavadoc = iMethod
						.getCompilationUnit()
						.getSource()
						.substring(
								javadocRange.getOffset(),
								javadocRange.getOffset()
										+ javadocRange.getLength());
				attachedJavadoc = attachedJavadoc.substring(3,
						attachedJavadoc.length() - 2);
				StringReader rr = new StringReader(attachedJavadoc);
				BufferedReader mm = new BufferedReader(rr);
				final StringBuilder bld = new StringBuilder();
				final HashMap<String, String> mmq = new HashMap<String, String>();
				while (true) {
					try {
						String s = mm.readLine();
						if (s == null) {
							break;
						}
						int indexOf = s.indexOf('*');
						if (indexOf != -1) {
							s = s.substring(indexOf + 1);
						}
						s = s.trim();
						if (s.startsWith("@")) {
							if (s.startsWith(PARAM)){
								s=s.substring(PARAM.length());
								s=s.trim();
								int p=s.indexOf(' ');
								if (p!=-1){
									String pName=s.substring(0,p).trim();
									String pVal=s.substring(p).trim();
									mmq.put(pName, pVal);
								}
							}	
							if (s.startsWith(RETURN)){
								s=s.substring(RETURN.length());
								s=s.trim();
								mmq.put(RETURN, s);
							}
							continue;
						}
						bld.append(s);
						bld.append('\n');
					} catch (IOException e) {
						break;
					}
				}
				return new IDocInfo() {

					@Override
					public String getDocumentation(String pName) {
						return mmq.get(pName);
					}

					@Override
					public String getDocumentation() {
						return bld.toString().trim();
					}

					@Override
					public String getReturnInfo() {
						return mmq.get(RETURN);
					}
				};
			}
			return new IDocInfo() {
				
				@Override
				public String getReturnInfo() {
					return "";
				}
				
				@Override
				public String getDocumentation(String pName) {
					return "";
				}
				
				@Override
				public String getDocumentation() {
					return "";
				}
			};
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

}
