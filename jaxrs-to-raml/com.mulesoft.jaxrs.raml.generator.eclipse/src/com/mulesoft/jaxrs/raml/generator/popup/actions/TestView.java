package com.mulesoft.jaxrs.raml.generator.popup.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class TestView extends ViewPart {

	GenerateRAML r=new GenerateRAML();
	
	ISelectionListener iSelectionListener = new ISelectionListener() {
		
		
		public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
			if (arg1!=null){
			r.selectionChanged(null, arg1);
			s.setEnabled(!arg1.isEmpty());
			}
		}
	};

	private Button s;
	public void dispose() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(iSelectionListener);
		super.dispose();
	};
	
	
	public void createPartControl(Composite arg0) {
		Composite mm=new Composite(arg0, SWT.NONE);
		mm.setLayout(new GridLayout());
		s = new Button(mm, SWT.PUSH);
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		r.selectionChanged(null, selection);
		s.setEnabled(selection!=null&&!selection.isEmpty());
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(iSelectionListener);
		s.setText("Generate RAML");
		s.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				r.run(null);
			}
		});
	}

	
	public void setFocus() {

	}

}
