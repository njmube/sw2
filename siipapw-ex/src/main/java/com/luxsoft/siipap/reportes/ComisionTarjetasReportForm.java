package com.luxsoft.siipap.reportes;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.model.Sucursal;
import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;



/**
 * 
 * @author Ruben Cancino
 *
 */
public class ComisionTarjetasReportForm extends SWXAction{
	
	

	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			ReportUtils.viewReport(ReportUtils.toReportesPath("tesoreria/ComisionTarjetas.jasper"), form.getParametros());
			
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
		
		private JComboBox sucursalControl;
		private JXDatePicker fechaCorte;
		//private JXDatePicker fechaFinal;
	//	private JComboBox origenBox;
		
		

		public ReportForm() {
			super("Comision Tarjetas");
		}
		
		private void initComponents(){
			fechaCorte=new JXDatePicker();
			fechaCorte.setFormats("dd/MM/yyyy");
			//fechaFinal=new JXDatePicker();
			//fechaFinal.setFormats("dd/MM/yyyy");
		//	Object[] values={OrigenDeOperacion.MOS,OrigenDeOperacion.CAM,OrigenDeOperacion.CRE};
		//	origenBox=new JComboBox(values);
			sucursalControl=createSucursalControl();
		}
		
		private JComponent buildForm(){
			initComponents();
			final FormLayout layout=new FormLayout(
					"p,3dlu,f:60dlu:g",
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Fecha Corte",fechaCorte);
			//builder.append("Fecha Final",fechaFinal);
		//	builder.append("Tipo",origenBox);
			builder.append("Sucursal",sucursalControl);
			return builder.getPanel();
		}
		
		private JComboBox createSucursalControl() {			
			final JComboBox box = new JComboBox(ServiceLocator2.getLookupManager().getSucursalesOperativas().toArray());
			Sucursal local=ServiceLocator2.getConfiguracion().getSucursal();
			for(int index=0;index<box.getModel().getSize();index++){
				Sucursal s=(Sucursal)box.getModel().getElementAt(index);
				if(s.equals(local)){
					box.setSelectedIndex(index);
					break;
				}
			}
			return box;
		}
		
		private int getSucursal(){
			Sucursal selected=(Sucursal)sucursalControl.getSelectedItem();
			return selected.getClave();
		}
		
		protected JComponent buildHeader(){
			return new Header("Relaci�n de Pagos con Tarjeta","").getHeader();
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
			parametros.put("FECHA_CORTE", fechaCorte.getDate());
			//parametros.put("FECHA_FIN", fechaFinal.getDate());
			//Sucursal suc=Services.getInstance().getConfiguracion().getSucursal();
			//parametros.put("SUCURSAL", String.valueOf(suc.getClave()));
			parametros.put("SUCURSAL", String.valueOf(getSucursal()));
		//	OrigenDeOperacion origen=(OrigenDeOperacion)origenBox.getSelectedItem();
		//	parametros.put("ORIGEN", origen.name());
			logger.info("Parametros de reporte:"+parametros);
			
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		 
		
	}
	
	public static void run(){
		ComisionTarjetasReportForm action=new ComisionTarjetasReportForm();
		action.execute();
	}
	

	
	public static void main(String[] args) {
		run();
	}

}
