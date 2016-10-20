/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package raml.jaxrs.eclipse.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;

public class ConfigurationDialog extends TitleAreaDialog {	
	
	public ConfigurationDialog(Shell parentShell, UIConfiguration uiConfig) {
		super(parentShell);
		this.setShellStyle( SWT.DIALOG_TRIM|SWT.RESIZE );
		this.parentShell = parentShell;
		this.uiConfig = uiConfig;	
		if (this.uiConfig.dstFolder.get()==null){
			this.uiConfig.dstFolder.set(dstFolder);
		}
	}
	
	private final Shell parentShell;
	
	private final UIConfiguration uiConfig;
	
	protected Composite createDialogArea( Composite parent ){
		
		this.setTitle("Convert RAML to JAX-RS");
		this.setMessage("Please, adjust convertation parameters.");
		parent.setLayout( new GridLayout(1,false) );
		Group hBox = new Group(parent,SWT.NONE);
		GridData hBoxData = new GridData( GridData.FILL_BOTH);
		hBoxData.widthHint = 300 ;
		hBoxData.heightHint = 400 ;
		hBox.setLayoutData( hBoxData ) ;
		hBox.setLayout( new GridLayout(1,false)) ;
		
		createTextField(hBox, "Base package name", this.uiConfig.basePackageName);
		
		createResourceSelectionGroup(hBox,"RAML file", this.uiConfig.ramlFile, "file");
		createResourceSelectionGroup(hBox,"Source folder", this.uiConfig.srcFolder, "folder");
		createResourceSelectionGroup(hBox,"Destination folder", this.uiConfig.dstFolder, "folder");
		
		createCombo(hBox, "JAX-RS Version", getJaxrsVersionRealm(), this.uiConfig.jaxrsVersion);
		createCombo(hBox, "JSON Mapper", getAnnotationStyleRealm(), this.uiConfig.jsonMapper);
		createCheckBox(hBox, "Generate client proxy code", this.uiConfig.generateClientProxy);
		createCheckBox(hBox, "Use JSR 303 Annotations", this.uiConfig.useJsr303Annotations);
		createCheckBox(hBox, "Map empty response to void", this.uiConfig.isEmptyResponseUsesVoid);
		return parent;	
	}

	private void createTextField(Group parent, String title, final ObjectReference<String> container)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(title);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3,false));
		
		final Text text = new Text(group, SWT.BORDER);
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.horizontalSpan = 2;
		text.setLayoutData(textData);
		
		if( container.get() != null ){
			text.setText( container.get() );
		}
		text.addModifyListener( new ModifyListener() {
			
			
			public void modifyText(ModifyEvent e) {
				container.set(text.getText());				
			}
		});
	}

	private void createResourceSelectionGroup(
			Composite parent,
			String title,
			final ObjectReference<IResource> container,
			final String filterType ) {
		
		Group group = new Group(parent, SWT.NONE);
		group.setText(title);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3,false));
		
		final Text pathText = new Text(group, SWT.BORDER);
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.horizontalSpan = 2;
		pathText.setLayoutData(textData);
		
		if( container.get() != null ){
			pathText.setText( container.get().getFullPath().toString() );
		}
		
		pathText.setEditable(false);
		
		final Button pathButton = new Button(group, SWT.PUSH);
		GridData buttonData = new GridData();
		buttonData.horizontalSpan = 1;
		pathButton.setLayoutData(buttonData);
		pathButton.setText("Browse...");
		
		pathButton.addSelectionListener( new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent e) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						parentShell, new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				
				dialog.setAllowMultiple(false);				
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());				
				setFilter(filterType, dialog);
				
				if( dialog.open() != Dialog.OK ){
					return;
				}
				
				Object[] result = dialog.getResult();
				if(result==null||result.length==0){
					return;
				}
				if(!(result[0] instanceof IResource)){
					return;
				}
				IResource selectedResource = (IResource) result[0];
				container.set(selectedResource);
				pathText.setText( container.get().getFullPath().toString() );
			}			
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}


	
	private void setFilter(final String filterType,	ElementTreeSelectionDialog dialog)
	{
		if(filterType.equals("file")){
			dialog.addFilter(new ViewerFilter() {
				
				
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					
					if(element==null){
						return false;
					}
					if(element instanceof IFile){
						IFile file = (IFile) element;
						return file.getName().toLowerCase().endsWith(".raml");
					}
					return true;
				}
			});
		}
		else if(filterType.equals("folder")){
			dialog.addFilter(new ViewerFilter() {
				
				
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					
					if(element==null){
						return false;
					}
					return element instanceof IContainer;
				}
			});
		}
	}
	
	private void createCombo(
			Composite parent,
			String title,
			final List<String> values,
			final ObjectReference<String> container ) {
		
		Composite group = new Composite(parent, SWT.NONE);		
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3,false));
		
		Label l = new Label(group, SWT.NONE);		
		l.setText(title);
		GridData lData = new GridData();
		l.setLayoutData(lData);		
		
		final Combo combo = new Combo(group, SWT.NONE);
		GridData comboData = new GridData();
		comboData.horizontalSpan = 1;
		combo.setLayoutData(comboData);
		
		combo.setItems(values.toArray(new String[values.size()]));
		
		Object currentValue = container.get();
		if( currentValue != null ){
			int indexOf = values.indexOf(currentValue);
			if(indexOf>=0){
				combo.select(indexOf);
			}
		}
		
		combo.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = combo.getSelectionIndex();				
				container.set(values.get(selectionIndex));
			}
			
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	private void createCheckBox(
			Composite parent,
			String title,
			final ObjectReference<Boolean> container ) {

		final Button button = new Button(parent, SWT.CHECK);
		button.setText(title);
		GridData data = new GridData();
		button.setLayoutData(data);
		
		Boolean currentValue = container.get();
		if( currentValue != null ){
			button.setSelection(currentValue);
		}
		
		button.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent e) {
				container.set(button.getSelection());
			}
			
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	private List<String> getJaxrsVersionRealm(){
		
		ArrayList<String> result=  new ArrayList<String>(Arrays.asList(			
			JaxrsVersion.JAXRS_1_1.name(),
			JaxrsVersion.JAXRS_2_0.name()
		)); 
				
		return result;
	}
	
	private List<String> getAnnotationStyleRealm(){
		
		ArrayList<String> result=  new ArrayList<String>(Arrays.asList(
				
			AnnotationStyle.GSON.name(),
			AnnotationStyle.JACKSON.name(),
			AnnotationStyle.JACKSON1.name(),
			AnnotationStyle.JACKSON2.name(),
			AnnotationStyle.NONE.name()
		)); 
				
		return result;
	}
	static IResource dstFolder;
	
	@Override
	protected void okPressed() {
		dstFolder=uiConfig.dstFolder.get();
		super.okPressed();
	}
}
