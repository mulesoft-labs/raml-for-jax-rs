package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.Utils;

/**
 * <p>APTMethodModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class APTMethodModel extends APTGenericElement implements IMethodModel {

	/**
	 * <p>Constructor for APTMethodModel.</p>
	 *
	 * @param x a {@link javax.lang.model.element.ExecutableElement} object.
	 */
	public APTMethodModel(ExecutableElement x, ProcessingEnvironment environment) {
		super(x,environment);
		this.element = x;
	}
	
	private boolean isGeneric;
	
	private ExecutableElement element;

	
	/**
	 * <p>getParameters.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IParameterModel} objects.
	 */
	public IParameterModel[] getParameters() {
		List<? extends VariableElement> parameters = element.getParameters();
		ArrayList<IParameterModel>result=new ArrayList<IParameterModel>();
		for (VariableElement q:parameters){
			result.add(new APTParameter(q,this.environment));
		}
		return result.toArray(new IParameterModel[result.size()]);
	}

	
	/**
	 * <p>getBasicDocInfo.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.IDocInfo} object.
	 */
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			public String getReturnInfo() {
				return Utils.extractReturnJavadoc(APTMethodModel.this.getDocumentation());
			}

			public String getDocumentation(String pName) {
				return Utils.extractParamJavadoc(APTMethodModel.this.getDocumentation(), pName);
			}

			public String getDocumentation() {
				return Utils.extractMethodJavadoc(APTMethodModel.this.getDocumentation());
			}
		};
	}

	
	/**
	 * <p>element.</p>
	 *
	 * @return a {@link javax.lang.model.element.Element} object.
	 */
	public Element element() {
		return element;
	}

	
	/**
	 * <p>getReturnedType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getReturnedType() {
		TypeMirror returnType = element.getReturnType();
		if (returnType != null && returnType instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) returnType;
			TypeElement returnTypeElement = (TypeElement) declaredType.asElement();
			return new APTType(returnTypeElement,this.environment);
		}
		return null;
	}

	
	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	
	/** {@inheritDoc} */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		APTMethodModel other = (APTMethodModel) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}
	
	
	/**
	 * <p>getBodyType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getBodyType() {		
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public ITypeModel getType() {
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isStatic() {
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isPublic() {
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public List<ITypeModel> getJAXBTypes() {
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public Class<?> getJavaType() {
		return null;
	}


	public boolean hasGenericReturnType() {
		return isGeneric;
	}


	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}


	@Override
	public boolean isCollection() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isMap() {
		// TODO Auto-generated method stub
		return false;
	}

}
