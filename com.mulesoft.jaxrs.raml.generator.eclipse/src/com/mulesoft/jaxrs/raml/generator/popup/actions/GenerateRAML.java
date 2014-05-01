package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.raml.emitter.RamlEmitterV2;
import org.raml.model.Raml2;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.visitor.PreservingTemplatesBuilder;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.jdt.JDTType;

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

	HashSet<IType> types = new HashSet<IType>();

	public Raml2 build(InputStream contents, ResourceLoader resourceLoader) {
		PreservingTemplatesBuilder preservingTemplatesBuilder = new PreservingTemplatesBuilder(
				resourceLoader, new TagResolver[] { new IncludeResolver() });
		Raml2 build2 = (Raml2) preservingTemplatesBuilder.build(contents);
		return build2;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		types.clear();
		boolean cpInited = false;
		IProject project = null;
		
		classLoader = null;
		try {
			for (Object q : selectionObject) {
				if (!cpInited) {
					IJavaElement el = (IJavaElement) q;
					IResource resource = el.getResource();
					if (project != null
							&& !project.equals(resource.getProject())) {
						MessageDialog.openInformation(shell,
								"Multiple Projects are not supported now",
								"Multiple Projects are not supported now");
						return;
					}
					project = resource.getProject();
					Collection<String> constructProjectClassPath = new ClassPathCollector()
							.constructProjectClassPath(project, true);
					URL[] urls = new URL[constructProjectClassPath.size()];
					int a = 0;
					for (String s : constructProjectClassPath) {
						urls[a++] = new File(s).toURL();
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
		boolean doSingle = isSingle;
		if (file == null) {
			return;
		}
		File outputFile = file.getLocation().toFile();
		File actualFile = outputFile;
		if (doSingle) {
			File createTempFile;
			try {
				createTempFile = File.createTempFile("temp", "raml");
				outputFile = createTempFile;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			// createTempFile.getParentFile();
		}

		visitor = new JDTResourceVisitor(outputFile, classLoader);
		for (Object q : selectionObject) {
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
			} catch (Exception e) {
				MessageDialog.openError(shell, e.getMessage(), e.getMessage());
			}
		}
		if (project != null) {
			if (!separateFiles){
				if (doSingle) {
				String raml = visitor.getRaml();
				Raml2 build = build(new ByteArrayInputStream(raml.getBytes()),
						new FileResourceLoader(outputFile.getParent()));
				RamlEmitterV2 emmitter = new RamlEmitterV2();
				emmitter.setSingle(true);
				String dump = emmitter.dump(build);
				try {
					save(dump, file);
				} catch (Exception e) {
					MessageDialog.openError(shell, e.getMessage(),
							e.getMessage());
				}
				return;
				}
				else{
				saveResult(visitor, file);
				}
			}
			try {
				project.refreshLocal(IProject.DEPTH_INFINITE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				MessageDialog.openError(shell, e.getMessage(), e.getMessage());
			}
		}

	}

	private void visitType(IType q) {
		if (!types.add(q)) {
			return;
		}
		IFile file = container.getFile(new Path(q.getElementName()+".raml"));
		if (separateFiles){
			visitor = new JDTResourceVisitor(file.getLocation().toFile(), classLoader)
			.createResourceVisitor();
			visitor.clear();
		}
		visitor.visit(new JDTType(q));
		if (separateFiles&&!visitor.isEmpty()){
			
			if (isSingle) {
				String raml = visitor.getRaml();
				Raml2 build = build(new ByteArrayInputStream(raml.getBytes()),
						new FileResourceLoader(container.getLocation().toFile()));
				RamlEmitterV2 emmitter = new RamlEmitterV2();
				emmitter.setSingle(true);
				String dump = emmitter.dump(build);
				try {
					save(dump, file);
				} catch (Exception e) {
					MessageDialog.openError(shell, e.getMessage(),
							e.getMessage());
				}
				return;
			}
			else{			
			saveResult(visitor, file);
			}
		}
	}

	private void visitUnit(ICompilationUnit q) throws JavaModelException {
		for (IType t : q.getAllTypes()) {
			visitType(t);
		}
	}

	private void visitPackage(IPackageFragment q) throws JavaModelException {
		ICompilationUnit[] compilationUnits = q.getCompilationUnits();
		for (ICompilationUnit unit : compilationUnits) {
			visitUnit(unit);
		}
	}

	private void visitPackageFragmentRoot(IPackageFragmentRoot pr)
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

	private void visitProject(IJavaProject q) throws JavaModelException {
		IPackageFragmentRoot[] packageFragmentRoots = q
				.getPackageFragmentRoots();
		for (IPackageFragmentRoot pr : packageFragmentRoots) {
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
	private boolean isSingle=false;
	private boolean separateFiles=false;

	IContainer container;
	private URLClassLoader classLoader;
	
	private IFile getNewRAMLFile(IProject project) {
		container=project;
		InputDialog inputDialog = new InputDialog(shell, "Generate RAML",
				"Please type file name for you raml file", "api.raml", null) {

			@Override
			protected Control createDialogArea(Composite parent) {
				Composite createDialogArea = (Composite) super.createDialogArea(parent);
				final Control[] children = createDialogArea.getChildren();
				if (separateFiles){
					for (Control c:children){
						c.setEnabled(false);
					}
					}
				Composite t=new Composite(createDialogArea, SWT.NONE);
				GridLayout gridLayout = new GridLayout(3,false);
				gridLayout.marginHeight=0;
				gridLayout.marginLeft=0;
				Label l=new Label(t,SWT.NONE);
				l.setText("Folder:");
				final Text ts=new Text(t, SWT.BORDER);
				ts.setEditable(false);
				
				ts.setText(container.getFullPath().toPortableString());
				Button browse=new Button(t,SWT.PUSH);
				browse.setText("...");
				browse.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ContainerSelectionDialog dlg=new ContainerSelectionDialog(getShell(), 
								ResourcesPlugin.getWorkspace().getRoot(), 
								true, "Please select folder to store raml files");
						int open = dlg.open();
						if (open==Dialog.OK){
							Object[] result = dlg.getResult();
							ts.setText(result[0].toString());
							IPath iPath = (IPath) result[0];
							container=(IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(iPath);
						}						
					}
				});
				GridDataFactory.fillDefaults().grab(true, false).applyTo(ts);
				GridDataFactory.fillDefaults().applyTo(t);
				t.setLayout(gridLayout);
				final Button bs = new Button(createDialogArea, SWT.CHECK);
				bs.setText("Inline schemas and example in single raml file");
				bs.setSelection(isSingle);
				bs.addSelectionListener(new SelectionAdapter() {
					

					@Override
					public void widgetSelected(SelectionEvent e) {
						isSingle=bs.getSelection();
					}
				});
				final Button bs1 = new Button(createDialogArea, SWT.CHECK);
				bs1.setText("Generate separate raml files for a different java classes");
				bs1.setSelection(separateFiles);
				
				//bs.setEnabled(!bs1.getSelection());
				bs1.addSelectionListener(new SelectionAdapter() {
					

					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean selection = bs1.getSelection();
						for (Control c:children){
							c.setEnabled(!selection);
						}
						separateFiles=selection;
						//bs.setEnabled(!selection);
					}
				});
				return createDialogArea;
			}

		};
		int open = inputDialog.open();
		if (open == Dialog.OK) {
			return container.getFile(new Path(inputDialog.getValue()));
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
