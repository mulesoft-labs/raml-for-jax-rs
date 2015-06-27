package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.generator.eclipse.JAXRSTORamlPlagin;
import com.mulesoft.jaxrs.raml.generator.popup.actions.GenerationException;

@SuppressWarnings("restriction")
public class JDTMethod extends JDTGenericElement implements IMethodModel {

	private static final String PARAM = "@param"; //$NON-NLS-1$
	private static final String RETURN = "@return"; //$NON-NLS-1$

	public JDTMethod(IMethod tm) {
		super(tm);
	}
	
	private boolean isGeneric;

	public String getName() {
		return ((IMethod) tm).getElementName();
	}

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
						if (s.startsWith("@")) { //$NON-NLS-1$
							if (s.startsWith(PARAM)) {
								s = s.substring(PARAM.length());
								s = s.trim();
								int p = s.indexOf(' ');
								if (p != -1) {
									String pName = s.substring(0, p).trim();
									String pVal = s.substring(p).trim();
									mmq.put(pName, pVal);
								}
							}
							if (s.startsWith(RETURN)) {
								s = s.substring(RETURN.length());
								s = s.trim();
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

					public String getDocumentation(String pName) {
						return mmq.get(pName);
					}

					public String getDocumentation() {
						return bld.toString().trim();
					}

					public String getReturnInfo() {
						return mmq.get(RETURN);
					}
				};
			}
			return new IDocInfo() {

				public String getReturnInfo() {
					return ""; //$NON-NLS-1$
				}

				public String getDocumentation(String pName) {
					return ""; //$NON-NLS-1$
				}

				public String getDocumentation() {
					return ""; //$NON-NLS-1$
				}
			};
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

	public ITypeModel getReturnedType() {
		IMethod iMethod = (IMethod) tm;
		try {
			String returnType = iMethod.getReturnType();
			ITypeModel doGetType = doGetType(iMethod, returnType);
			return doGetType;

		} catch (Exception e) {
			if (e instanceof GenerationException) {
				throw (GenerationException)e;
			}
			throw new IllegalStateException(e);
		}
	}

	

	
	public ITypeModel getBodyType() {
		IMethod iMethod = (IMethod) tm;
		try {
			String[] parameterTypes = iMethod.getParameterTypes();
			for (String s : parameterTypes) {
				if (s.contains("java")) //$NON-NLS-1$
				{
					continue;
				}
				String returnType = s;
				if (returnType.startsWith("Q") && returnType.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
					IType ownerType = (IType) iMethod
							.getAncestor(IJavaElement.TYPE);
					String[][] resolveType = ownerType.resolveType(returnType
							.substring(1, returnType.length() - 1));
					if (resolveType.length == 1) {
						IType findType = ownerType.getJavaProject().findType(
								resolveType[0][0] + '.' + resolveType[0][1]);
						if (findType != null && findType instanceof SourceType) {
							return new JDTType(findType);
						}
					}

				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}
	@Override
	public boolean isStatic() {
		try {
			return Flags.isStatic(((IMember)tm).getFlags());
		} catch (JavaModelException e) {
			return false;
		}
	}

	@Override
	public boolean isPublic() {
		try {
			return Flags.isPublic(((IMember)tm).getFlags());
		} catch (JavaModelException e) {
			return false;
		}
	}

	@Override
	public ITypeModel getType() {
		return getReturnedType();
	}

	@Override
	public List<ITypeModel> getJAXBTypes() {
		try {
			List<ITypeModel> list = doGetJAXBTypes(((IMember)tm), ((IMethod)tm).getReturnType());
			return list;
		} catch (JavaModelException e) {
			return null;
		}
	}

	@Override
	public Class<?> getJavaType() {
		try {
			String returnType = ((IMethod)tm).getReturnType();
			return getBasicJavaType(returnType);
		} catch (JavaModelException e) {
			return null;
		}
	}

	public boolean hasGenericReturnType() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}
	
	@Override
	public boolean isCollection() {
		try {
			return isCollection(((IMember)tm), ((IMethod)tm).getReturnType());
		} catch (JavaModelException e) {
			return false;
		}
	}

	@Override
	public boolean isMap() {
		try {
			return isMap(((IMember)tm), ((IMethod)tm).getReturnType());
		} catch (JavaModelException e) {
			return false;
		}
	}

	
}
