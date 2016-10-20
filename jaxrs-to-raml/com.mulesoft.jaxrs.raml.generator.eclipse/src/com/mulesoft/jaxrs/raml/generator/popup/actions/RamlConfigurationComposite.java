package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.raml.model.ActionType;
import org.raml.model.Protocol;

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;

public class RamlConfigurationComposite extends Composite{

	IRamlConfig config;
	ArrayList<AbstractEditor>editors=new ArrayList<RamlConfigurationComposite.AbstractEditor>();
	private Button http;
	private Button https;
	private Button sorted;
	private Button doFull;
	
	public void doOk(){
		for(AbstractEditor e:editors){
			e.save((IEditableRamlConfig) config, e.getValue());
		}
		HashSet<Protocol>p=new HashSet<Protocol>();
		if(http.getSelection()){
			p.add(Protocol.HTTP);
		}
		if(https.getSelection()){
			p.add(Protocol.HTTPS);
		}
		((IEditableRamlConfig) config).setProtocols(p);
		((IEditableRamlConfig) config).setSorted(sorted.getSelection());
		((IEditableRamlConfig) config).setDoFullTree(!doFull.getSelection());
		for (ActionType a:actionType_To_Code.keySet()){
			Text t=actionType_To_Code.get(a);
			((IEditableRamlConfig) config).setDefaultResponseCode(a,t.getText());
		}
	}
	
	public abstract class AbstractEditor{
		
		public AbstractEditor() {
			editors.add(this);
		}
		
		abstract Object init(IEditableRamlConfig cfg);
		abstract void save(IEditableRamlConfig cfg,Object value);
		abstract Object getValue();
	}
	
	public abstract class StringEditor extends AbstractEditor{
		
		private Text text;

		public StringEditor(Composite parent,String titleL) {
			Label title=new Label(parent, SWT.NONE);
			title.setText(titleL);
			text = new Text(parent, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
			text.setText((String) init((IEditableRamlConfig) config));
		}
		
		Object getValue() {
			return text.getText();
		}
	}
	
	public abstract class CheckBoxEditor extends AbstractEditor{
		
		private Button text;

		public CheckBoxEditor(Composite parent,String titleL) {
			text = new Button(parent, SWT.CHECK);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
			text.setSelection((Boolean)init((IEditableRamlConfig) config));
			text.setText(titleL);
		}
		
		Object getValue() {
			return text.getSelection();
		}
	}
	
	public RamlConfigurationComposite(Composite parent, int style,IRamlConfig cfg) {
		super(parent, style);
		this.config=cfg;
		CTabFolder fld=new CTabFolder(this, SWT.FLAT);
		CTabItem item=new CTabItem(fld, SWT.NONE);
		item.setText("Basic Settings");
		item.setControl(generateBasicTab(fld));
		
		item=new CTabItem(fld, SWT.NONE);
		item.setText("Response Codes");
		item.setControl(generateResponseCodesTab(fld));
		setLayout(new FillLayout());
		fld.setSelection(0);
		
	}
	HashMap<ActionType, Text>actionType_To_Code=new HashMap<ActionType, Text>();
	private Button singleFile;

	private Control generateResponseCodesTab(CTabFolder fld) {
		Composite c=new Composite(fld, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		Label l=new Label(c, SWT.NONE);
		l.setText("Default response codes:");
		GridDataFactory.fillDefaults().span(2, 1).applyTo(l);
		for(ActionType a:ActionType.values()){
			Label la=new Label(c, SWT.NONE);
			la.setText(a.name()+":");
			Text t=new Text(c, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(t);
			String responseCode = config.getResponseCode(a);
			if (responseCode==null)
			{
				responseCode="200";
			}
			t.setText(responseCode);
			actionType_To_Code.put(a, t);
		}
		return c;
	}

	private Control generateBasicTab(CTabFolder fld) {
		Composite c=new Composite(fld, SWT.NONE);
		c.setLayout(new GridLayout(2,false));
		new StringEditor(c,"API Title:") {
			

			
			void save(IEditableRamlConfig cfg, Object value) {
				cfg.setTitle((String) value);
			}

			
			Object init(IEditableRamlConfig cfg) {
				return cfg.getTitle();
			}
		};
		new StringEditor(c,"API Version:") {
			

			
			void save(IEditableRamlConfig cfg, Object value) {
				cfg.setVersion((String) value);
			}

			
			Object init(IEditableRamlConfig cfg) {
				return cfg.getVersion();
			}
		};
		new StringEditor(c,"Base url:") {
			

			
			void save(IEditableRamlConfig cfg, Object value) {
				cfg.setBaseUrl((String) value);
			}

			
			Object init(IEditableRamlConfig cfg) {
				return cfg.getBaseUrl();
			}
		};
		Composite cm=new Composite(c, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth=0;
		gridLayout.marginHeight=0;
		Set<Protocol> protocols = config.getProtocols();
		http = new Button(cm, SWT.CHECK);
		if (protocols.contains(Protocol.HTTP)){
			http.setSelection(true);
		}
		http.setText("HTTP");
		https = new Button(cm, SWT.CHECK);
		https.setText("HTTPS");
		
		if (protocols.contains(Protocol.HTTPS)){
			https.setSelection(true);
		}
		cm.setLayout(gridLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cm);
		sorted = new Button(c, SWT.CHECK);
		sorted.setText("Sort resources alphabetically");
		sorted.setSelection(config.isSorted());
		doFull = new Button(c, SWT.CHECK);
		doFull.setText("Skip resources with no methods");
		doFull.setSelection(!config.doFullTree());
		
		singleFile = new Button(c, SWT.CHECK);
		singleFile.setText("Generate schemas and examples in a single RAML file");
		singleFile.setSelection(config.isSingle());
		oldSingleFileSelection=config.isSingle();
		singleFile.addSelectionListener(new SelectionAdapter() {
			

			
			public void widgetSelected(SelectionEvent e) {
				config.setSingle(singleFile.getSelection());
			}
		});
		
		
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(singleFile);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(sorted);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(doFull);
		return c;
	}
	
	boolean oldSingleFileSelection;

	public void setSeparateFilesSelected(boolean selection) {
		singleFile.setEnabled(!selection);
		if (selection){
			oldSingleFileSelection=singleFile.getSelection();
			singleFile.setSelection(false);
		}
		else{
			singleFile.setSelection(oldSingleFileSelection);
		}
	}
	

}
