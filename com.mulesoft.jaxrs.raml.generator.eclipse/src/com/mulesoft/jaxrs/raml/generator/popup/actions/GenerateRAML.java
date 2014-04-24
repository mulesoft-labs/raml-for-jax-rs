package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitorFactory;

public class GenerateRAML implements IObjectActionDelegate {

	private Shell shell;
	private List<?> selectionObject;
	private ResourceVisitor visitor;

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

	HashSet<IType>types=new HashSet<IType>();
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		types.clear();
		boolean cpInited=false;	
		IProject project=null;
		URLClassLoader classLoader = null;
		try {
			for (Object q : selectionObject) {
				if (!cpInited)
				{
					IJavaElement el=(IJavaElement) q;
					IResource resource = el.getResource();
					if( project!=null&&!project.equals(resource.getProject())){
						MessageDialog.openInformation(shell, "Multiple Projects are not supported now","Multiple Projects are not supported now");
						return;
					}
					project = resource.getProject();
					Collection<String> constructProjectClassPath = new ClassPathCollector().constructProjectClassPath(project, true);
					URL[] urls=new URL[constructProjectClassPath.size()];
					int a=0;
					for (String s:constructProjectClassPath)
					{
						urls[a++]=new File(s).toURL();
					}		
					
					classLoader = new URLClassLoader(urls);
					cpInited = true;
				}
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IFile file = getNewRAMLFile(project);
		if (file == null) {
			return;
		}
		File outputFile = file.getLocation().toFile();
		visitor = new RuntimeResourceVisitorFactory(outputFile, classLoader)
				.createResourceVisitor();
		for (Object q : selectionObject) {
		try{
			if (q instanceof IType){
				visitType((IType) q);
			}
			if (q instanceof IPackageFragment){
				visitPackage((IPackageFragment) q);
			}
			if (q instanceof IPackageFragmentRoot){
				visitPackageFragmentRoot((IPackageFragmentRoot) q);
			}
			if (q instanceof IJavaProject){
				visitProject((IJavaProject) q);
			}
			if (q instanceof ICompilationUnit) {
				visitUnit((ICompilationUnit) q);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
		if (project!=null){
			saveResult(visitor, file);
		}
	}

	private void visitType(IType q) {
		if (!types.add(q)){
			return;
		}
		visitor.visit(new JDTType(q));
	}
	private void visitUnit(ICompilationUnit q) throws JavaModelException {
		for (IType t:q.getAllTypes())
		{
			visitType(t);
		}
	}
	private void visitPackage(IPackageFragment q) throws JavaModelException {
		ICompilationUnit[] compilationUnits = q.getCompilationUnits();
		for (ICompilationUnit unit:compilationUnits){
			visitUnit(unit);
		}
	}
	private void visitPackageFragmentRoot(IPackageFragmentRoot pr) throws JavaModelException {
		if (pr.getKind()==IPackageFragmentRoot.K_SOURCE){
			IJavaElement[] children = pr.getChildren();
			for (IJavaElement z:children){
				if (z instanceof IPackageFragment){
					visitPackage((IPackageFragment) z);
				}
			}
		}
	}
	private void visitProject(IJavaProject q) throws JavaModelException {
		IPackageFragmentRoot[] packageFragmentRoots = q.getPackageFragmentRoots();
		for (IPackageFragmentRoot pr:packageFragmentRoots){
			visitPackageFragmentRoot(pr);
		}
	}

	private void saveResult(ResourceVisitor visitor, IFile file) {
		String raml = visitor.getRaml();
		try {
			save(raml, file);
		} catch (UnsupportedEncodingException e) {
			MessageDialog.openError(shell, "Error", e.getMessage());

		} catch (CoreException e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
	}
	
	private IFile getNewRAMLFile(IProject project){
		InputDialog inputDialog = new InputDialog(shell, "Save RAML to",
				"Please type file name for you raml file", "api.raml", null);
		int open = inputDialog.open();
		if (open == Dialog.OK) {
			return project.getFile(inputDialog.getValue());
		}
		return null;
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
		selectionObject = ((IStructuredSelection) selection).toList();
	}

}