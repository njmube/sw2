package com.luxsoft.siipap.pos.ui.reports;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import com.luxsoft.siipap.cxc.model.OrigenDeOperacion;
import com.luxsoft.siipap.model.Sucursal;
import com.luxsoft.siipap.pos.ui.utils.ReportUtils2;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

import com.luxsoft.sw3.services.Services;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class SaldosAFavorReportForm extends SWXAction{
	
	

	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			ReportUtils2.runReport("ventas/DisponiblesSucursal.jasper", form.getParametros());
		}
		form.dispose();
	}
	
	/**
	 * Forma para el reporte de cobranza
	 * 
	 * @author RUBEN
	 *
	 */
	public  class ReportForm extends SXAbstractDialog{
		
		private final Map<String, Object> parametros=new HashMap<String, Object>();
		
		
		private JXDatePicker fecha;
		
		
		

		public ReportForm() {
			super("Cobranza");
		}
		
		private void initComponents(){
			fecha=new JXDatePicker();
			fecha.setFormats("dd/MM/yyyy");
			
			
			
		}
		
		private JComponent buildForm(){
			initComponents();
			final FormLayout layout=new FormLayout(
					"p,3dlu,f:60dlu:g",
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Fecha ",fecha);
			
			
			return builder.getPanel();
		}
		
		protected JComponent buildHeader(){
			return new Header("Saldos a Favor del d�a","").getHeader();
		}

		@Override
		protected JComponent buildContent() {			
			JPanel panel=new JPanel(new BorderLayout());			
			panel.add(buildForm(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			parametros.put("FECHA", fecha.getDate());
			
			Sucursal suc=Services.getInstance().getConfiguracion().getSucursal();
			parametros.put("SUCURSAL", suc.getId());
			
			
			logger.info("Parametros de reporte:"+parametros);
			
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		 
		
	}
	
	public static void run(){
		SaldosAFavorReportForm action=new SaldosAFavorReportForm();
		action.execute();
	}
	
	public static void main(String[] args) {
		run();
	}

}
