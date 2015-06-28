package com.mulesoft.jaxrs.raml.generator.popup.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class SourceVisitor {
	
	public void visitObject(Object q) {
		if (!(q instanceof IJavaElement)){
			return;
		}
		try {
			if (q instanceof IType) {
				visitType((IType) q);
			}
			if (q instanceof IPackageFragment) {
				visitPackage((IPackageFragment) q);
			}
			if (q instanceof IPackageFragmentRoot) {
				visitPackageFragmentRoot((IPackageFragmentRoot) q);
			}
			if (q instanceof IJavaProject) {
				visitProject((IJavaProject) q);
			}
			if (q instanceof ICompilationUnit) {
				visitUnit((ICompilationUnit) q);
			}
		} catch (GenerationException e) {
			processGenerationException(e);
		} catch (Exception e) {
			processException(e);
		}
	}
	
	protected void processGenerationException(GenerationException e) {}
	
	protected void processException(Exception e) {}

	protected void visitType(IType q) {}

	protected void visitUnit(ICompilationUnit q) throws JavaModelException {
		for (IType t : q.getAllTypes()) {
			visitType(t);
		}
	}

	protected void visitPackage(IPackageFragment q) throws JavaModelException {
		ICompilationUnit[] compilationUnits = q.getCompilationUnits();
		for (ICompilationUnit unit : compilationUnits) {
			visitUnit(unit);
		}
	}

	protected void visitPackageFragmentRoot(IPackageFragmentRoot pr)
			throws JavaModelException {
		if (pr.getKind() == IPackageFragmentRoot.K_SOURCE) {
			IJavaElement[] children = pr.getChildren();
			for (IJavaElement z : children) {
				if (z instanceof IPackageFragment) {
					visitPackage((IPackageFragment) z);
				}
			}
		}
	}

	protected void visitProject(IJavaProject q) throws JavaModelException {
		IPackageFragmentRoot[] packageFragmentRoots = q
				.getPackageFragmentRoots();
		for (IPackageFragmentRoot pr : packageFragmentRoots) {
			visitPackageFragmentRoot(pr);
		}
	}

}
