package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.jdt.JDTType;

public class GenerateRAML implements IObjectActionDelegate {

	private Shell shell;
	private Object selectionObject;

	/**
	 * Constructor for Action1.
	 */
	public GenerateRAML() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();

	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		ResourceVisitor visitor = new ResourceVisitor();
		if (selectionObject instanceof IType) {
			IType t = (IType) selectionObject;
			visitor.visit(new JDTType(t));
			saveResult(visitor, t);
			return;
		}
		if (selectionObject instanceof ICompilationUnit) {
			ICompilationUnit unit = (ICompilationUnit) selectionObject;
			IType[] allTypes;
			try {
				allTypes = unit.getAllTypes();
				for (IType t : allTypes) {
					visitor.visit(new JDTType(t));
				}
			} catch (JavaModelException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			}
			saveResult(visitor, unit);
		}
		IResource q = (IResource) selectionObject;

		IProject project = q.getProject();
		IJavaProject create = JavaCore.create(project);

		if (create.exists()) {
			IJavaElement create2 = JavaCore.create(q);
			try {
				IPackageFragmentRoot[] packageFragmentRoots = create
						.getPackageFragmentRoots();
				for (IPackageFragmentRoot qq : packageFragmentRoots) {
					if (qq.getKind() == IPackageFragmentRoot.K_SOURCE) {
						IJavaElement[] children = qq.getChildren();
						for (IJavaElement z : children) {
							if (z instanceof IPackageFragment) {
								IPackageFragment pp = (IPackageFragment) z;
								ICompilationUnit[] compilationUnits = pp
										.getCompilationUnits();
								for (ICompilationUnit unit : compilationUnits) {
									IType[] allTypes = unit.getAllTypes();
									for (IType t : allTypes) {
										visitor.visit(new JDTType(t));
									}
								}
							}
						}
					}
				}
				String raml = visitor.getRaml();
				String name = "api.raml";
				IFile file = project.getFile(name);
				save(raml, file);
			} catch (Exception e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			}
		}
	}

	private void saveResult(ResourceVisitor visitor, IJavaElement t) {
		String raml = visitor.getRaml();
		InputDialog inputDialog = new InputDialog(shell, "Save RAML to",
				"Please type file name for you raml file", "api.raml", null);
		int open = inputDialog.open();
		if (open == Dialog.OK) {
			try {
				save(raml,
						t.getJavaProject().getProject()
								.getFile(inputDialog.getValue()));
			} catch (UnsupportedEncodingException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());

			} catch (CoreException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			}
		}
	}

	private void save(String raml, IFile file) throws CoreException,
			UnsupportedEncodingException {
		if (!file.exists()) {
			file.create(new ByteArrayInputStream(raml.getBytes("UTF-8")), true,
					new NullProgressMonitor());
		} else {
			file.setContents(new ByteArrayInputStream(raml.getBytes("UTF-8")),
					0, new NullProgressMonitor());
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectionObject = ((IStructuredSelection) selection).getFirstElement();
	}

}