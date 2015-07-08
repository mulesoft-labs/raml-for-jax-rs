package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.jdt.JDTType;

public class GenerateRAML implements IObjectActionDelegate {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final class RAMLConfigurationDialog extends InputDialog {
		private RamlConfigurationComposite ramlConfigurationComposite;

		private RAMLConfigurationDialog(Shell parentShell, String dialogTitle,
				String dialogMessage, String initialValue,
				IInputValidator validator) {
			super(parentShell, dialogTitle, dialogMessage, initialValue,
					validator);
		}
		
		protected void buttonPressed(int buttonId) {
			if (buttonId==Dialog.OK){
				ramlConfigurationComposite.doOk();
			}
			super.buttonPressed(buttonId);
		}
		
		protected void okPressed() {
			ramlConfigurationComposite.doOk();
			super.okPressed();
		}

		
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
				
				public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dlg=new ContainerSelectionDialog(getShell(), 
							ResourcesPlugin.getWorkspace().getRoot(), 
							true, "Please select folder to store RAML files");
					int open = dlg.open();
					if (open==Dialog.OK){
						Object[] result = dlg.getResult();
						ts.setText(result[0].toString());
						IPath iPath = (IPath) result[0];
						container=(IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(iPath);
					}						
				}
			});
			final Button bs1 = new Button(t, SWT.CHECK);
			bs1.setText("Generate an individual RAML file per each Java Class");
			bs1.setSelection(separateFiles);
			
			//bs.setEnabled(!bs1.getSelection());
			bs1.addSelectionListener(new SelectionAdapter() {
				

				
				public void widgetSelected(SelectionEvent e) {
					boolean selection = bs1.getSelection();
					for (Control c:children){
						c.setEnabled(!selection);
					}
					ramlConfigurationComposite.setSeparateFilesSelected(selection);
					separateFiles=selection;
					//bs.setEnabled(!selection);
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(ts);
			GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(bs1);
			GridDataFactory.fillDefaults().applyTo(t);
			ramlConfigurationComposite = new RamlConfigurationComposite(t, SWT.NONE,new PreferencesConfig());
			ramlConfigurationComposite.setSeparateFilesSelected(separateFiles);
			GridDataFactory.fillDefaults().grab(true, true).span(3,1).applyTo(ramlConfigurationComposite);
			t.setLayout(gridLayout);

			
			return createDialogArea;
		}
		
	}
	private boolean isSingle() {
		return new PreferencesConfig().isSingle();
	}

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
	
	IProgressMonitor monitor;

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
				if (!(q instanceof IJavaElement)){
					continue;
				}
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
		if (project==null){
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Nothing selected", "Please select some Java elements");
			return;
		}
		final IFile file = getNewRAMLFile(project);
		final boolean doSingle = isSingle();
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
		final File _outputFile = outputFile;
		final IProject _project = project;
		
		final SourceVisitor sourceVisitor = new SourceVisitor(){
			@Override
			protected void visitType(IType q) {
				GenerateRAML.this.visitType(q);
			}
			
			@Override
			protected void processException(Exception e) {				
				showException(e);
			}

			@Override
			protected void processGenerationException(GenerationException e) {
				showException(e);
			}
		};
		
		final int[] count = new int[1];
		final SourceVisitor countVisitor = new SourceVisitor(){
			@Override
			protected void visitType(IType q) {
				count[0]++;
			}

			@Override
			protected void processException(Exception e) {
				showException(e);
			}
			
			@Override
			protected void processGenerationException(GenerationException e) {
				showException(e);
			}
		};
		for (Object q : selectionObject) {
			countVisitor.visitObject(q);
		}
		
		final Job job = new Job("Generate RAML from JAX RS classes") {
			 
		@Override
		protected IStatus run(IProgressMonitor monitor) {
		
		GenerateRAML.this.monitor = monitor;
		monitor.beginTask("Generating RAML from JAX RS classes", count[0]+1);
		monitor.worked(1);

		visitor = new JDTResourceVisitor(_outputFile, classLoader);
		visitor.setPreferences(new PreferencesConfig());		
		for (Object q : selectionObject) {
			sourceVisitor.visitObject(q);
		}
		if (_project != null) {
			if (!separateFiles){
				if (doSingle) {
				String raml = visitor.getRaml();
				Raml2 build = build(new ByteArrayInputStream(raml.getBytes()),
						new FileResourceLoader(_outputFile.getParent()));
				RamlEmitterV2 emmitter = new RamlEmitterV2();
				emmitter.setSingle(true);
				String dump = emmitter.dump(build);
				try {
					save(dump, file);
				} catch (Exception e) {
					showException(e);
				}
				return Status.OK_STATUS;
				}
				else{
				saveResult(visitor, file);
				}
			}
			try {
				_project.refreshLocal(IProject.DEPTH_INFINITE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				showException(e);
			}
		}
		
		return Status.OK_STATUS;
		}
		};
		job.schedule();				
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
			
			if (isSingle()) {
				String raml = visitor.getRaml();
				Raml2 build = build(new ByteArrayInputStream(raml.getBytes()),
						new FileResourceLoader(container.getLocation().toFile()));
				RamlEmitterV2 emmitter = new RamlEmitterV2();
				emmitter.setSingle(true);
				String dump = emmitter.dump(build);
				try {
					save(dump, file);
				} catch (Exception e) {
					showException(e);
				}
				return;
			}
			else{			
			saveResult(visitor, file);
			}
		}
		this.monitor.worked(1);
	}

	private void saveResult(ResourceVisitor visitor, IFile file) {
		String raml = visitor.getRaml();
		try {
			save(raml, file);
		} catch (UnsupportedEncodingException e) {
			showException(e, "Error");
		} catch (CoreException e) {
			showException(e, "Error");
		}
	}
	
	private boolean separateFiles=false;

	IContainer container;
	private URLClassLoader classLoader;
	
	private IFile getNewRAMLFile(IProject project) {
		container=project.getFolder(new Path("raml"));
		InputDialog inputDialog = new RAMLConfigurationDialog(shell, "Generate RAML", "Please type the file name for your RAML file", "api.raml", null);
		int open = inputDialog.open();
		if (open == Dialog.OK) {
			return container.getFile(new Path(inputDialog.getValue()));
		}
		return null;
	}

	private void save(String raml, IFile file) throws CoreException,
			UnsupportedEncodingException {
		if (!file.exists()) {
			createFolder(file.getParent());
			file.create(new ByteArrayInputStream(raml.getBytes("UTF-8")), true,
					new NullProgressMonitor());
		} else {
			file.setContents(new ByteArrayInputStream(raml.getBytes("UTF-8")),
					0, new NullProgressMonitor());
		}
	}
	
	public void createFolder(IContainer folder) throws CoreException {
		if(folder==null||!(folder instanceof IFolder)){
			return;
		}
	    if (!folder.exists()) {
	        createFolder(folder.getParent());
	        ((IFolder)folder).create(true,true,new NullProgressMonitor());
	    }
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectionObject = ((IStructuredSelection) selection).toList();
	}
	

	private void showException(final Exception e, String... message) {
		
		String msg = "";
		if(e instanceof GenerationException){
			msg = ((GenerationException)e).getShortMessage();
		}
		else{
			msg = e.getMessage();
			if(message!=null&&message.length>0&&message[0]!=null){
				msg = message[0];
			}
			if(msg==null){
				msg = e.getClass().getCanonicalName();
			}
		}
		final String fMsg = msg;
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String text = "";
		if(e instanceof GenerationException){
			text = ((GenerationException)e).getDetailMessage();
			text += LINE_SEPARATOR;
			text += LINE_SEPARATOR;
		}
		text += sw.toString();
		final String fText = text;
				
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {				
				ErrorDialog dialog = new ErrorDialog(shell, "Generate RAML Error", fMsg, fText);
				dialog.open();
			}
		});
	}
	
	private static class ErrorDialog extends TitleAreaDialog{

		public ErrorDialog(Shell parentShell, String title, String message, String text) {
			super(parentShell);
			this.title = title;
			this.message = message;
			this.text = text;
		}
		private String title;
		
		private String message;
		
		private String text;
		
		@Override
		protected Control createDialogArea(Composite parent) {
			if(this.title!=null){
				this.setTitle(this.title);
			}
			if(this.message!=null){
				this.setMessage(this.message);
			}
			Composite area = (Composite) super.createDialogArea(parent);
		    Composite container = new Composite(area, SWT.NONE);
		    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		    GridLayout layout = new GridLayout(1, false);
		    container.setLayout(layout);
		    
		    Text txt = new Text(container, SWT.BORDER|SWT.MULTI);
		    txt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		    if(this.text!=null){
		    	txt.setText(this.text);
		    }
		    txt.setEditable(false);
		    return area;			
		}
	}
}
