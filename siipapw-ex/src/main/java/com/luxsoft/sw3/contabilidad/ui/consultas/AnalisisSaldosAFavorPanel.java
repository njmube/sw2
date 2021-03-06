package com.luxsoft.sw3.contabilidad.ui.consultas;

import java.sql.Types;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.model.CantidadMonetaria;
import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.browser.FilteredBrowserPanel;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.util.DBUtils;
import com.luxsoft.siipap.util.DateUtil;




public class AnalisisSaldosAFavorPanel extends FilteredBrowserPanel<AnalisisSaldosAFavorPanel.SaldoAFavorModel>{
	
	private Date fecha;
	
	public AnalisisSaldosAFavorPanel() {
		super(SaldoAFavorModel.class);
		setTitle("Saldos a favor");
	}
	
	public void init(){
		addProperty("TIPO"
				,"CLAVE"
				,"NOMBRE"
				,"FECHA"
				,"SUCURSAL"				
				,"ORIGEN"
				,"CONCEPTO"
				,"TOTAL"
				,"PAGOS"
				,"DISPONIBLE"
				,"BANCO"
				,"DESCRIPCION"
				);
		installTextComponentMatcherEditor("Sucursal", "SUCURSAL");
		installTextComponentMatcherEditor("Origen", "ORIGEN");
		installTextComponentMatcherEditor("Cliente", "NOMBRE");
	}

	@Override
	protected List<SaldoAFavorModel> findData() {
		String sql="select 'SALDO_A_FAVOR' AS TIPO,A.SAF AS FECHA,(SELECT S.NOMBRE FROM sw_sucursales S WHERE A.SUCURSAL_ID=S.SUCURSAL_ID) AS SUCURSAL,A.CLAVE,A.NOMBRE"
			+",(SELECT MAX(X.CAR_ORIGEN) FROM sx_cxc_aplicaciones X WHERE X.ABONO_ID=A.ABONO_ID) AS ORIGEN" 
			+ ",CONCAT(a.TIPO_ID,' REF.:',(CASE WHEN A.TIPO_ID='PAGO_DEP' THEN A.REFERENCIA WHEN A.TIPO_ID='PAGO_CHE' THEN A.numero  WHEN A.TIPO_ID='PAGO_TAR' THEN A.AUTO_TARJETA_BANCO ELSE '' END)) as CONCEPTO"
			+",A.TOTAL,IFNULL((SELECT SUM(X.IMPORTE) FROM sx_cxc_aplicaciones X WHERE X.ABONO_ID=A.ABONO_ID AND X.fecha=a.saf),0) as PAGOS"
			+",(A.TOTAL-IFNULL((SELECT (X.DIFERENCIA) FROM sx_cxc_abonos X WHERE X.ABONO_ID=A.ABONO_ID AND X.DIFERENCIA_FECHA=A.SAF),0)-IFNULL((SELECT SUM(X.IMPORTE) FROM sx_cxc_aplicaciones X WHERE X.ABONO_ID=A.ABONO_ID AND X.fecha=A.SAF),0) )AS DISPONIBLE"
			+",(SELECT C.DESCRIPCION FROM sw_cuentas C WHERE C.ID=A.CUENTA_ID) AS BANCO,substr(a.TIPO_ID,6,3) AS DESCRIPCION"
			+" from sx_cxc_abonos a where a.SAF=? AND A.TIPO_ID LIKE 'PAGO%' AND "
			+" (A.TOTAL-IFNULL((SELECT (X.DIFERENCIA) FROM sx_cxc_abonos X WHERE X.ABONO_ID=A.ABONO_ID AND X.DIFERENCIA_FECHA=A.SAF),0)-IFNULL((SELECT SUM(X.IMPORTE) FROM sx_cxc_aplicaciones X WHERE X.ABONO_ID=A.ABONO_ID AND X.fecha=A.SAF),0) )>0";
		Object[] params=new Object[]{new SqlParameterValue(Types.DATE,getFecha())};
		return ServiceLocator2.getJdbcTemplate().query(sql, params, new BeanPropertyRowMapper(SaldoAFavorModel.class));
	}
	public void open(){
		load();
	}
	
	@Override
	protected void executeLoadWorker(SwingWorker worker) {
		TaskUtils.executeSwingWorker(worker);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}


	


	@Override
	public Action[] getActions() {
		return new Action[0];
	}
	
private TotalesPanel totalPanel;
	
	
	public JPanel getTotalesPanel(){
		if(totalPanel==null){
			totalPanel=new TotalesPanel();
		}
		return (JPanel)totalPanel.getControl();
	}
	
	
	private class TotalesPanel extends AbstractControl implements ListEventListener{
		
		private JLabel ventaTotal=new JLabel();

		@Override
		protected JComponent buildContent() {
			final FormLayout layout=new FormLayout("p,2dlu,f:p:g","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			ventaTotal.setHorizontalAlignment(SwingConstants.RIGHT);
			builder.append("Total",ventaTotal);
			
			builder.getPanel().setOpaque(false);
			getFilteredSource().addListEventListener(this);
			updateTotales();
			return builder.getPanel();
		}
		
		public void listChanged(ListEvent listChanges) {
			if(listChanges.next()){
				
			}
			updateTotales();
		}
		
		public void updateTotales(){
			
			CantidadMonetaria total=CantidadMonetaria.pesos(0);
			for(Object o:getFilteredSource()){
				SaldoAFavorModel v=(SaldoAFavorModel)o;
				total=total.add(CantidadMonetaria.pesos(v.getTOTAL().doubleValue()));
			}			
			String pattern="{0}";
			ventaTotal.setText(MessageFormat.format(pattern, total.amount()));
		}
	}



	public static class SaldoAFavorModel{
		private String TIPO;
		private Date FECHA;
		private String SUCURSAL;
		private String CLAVE;
		private String NOMBRE;
		private String ORIGEN;
		private String CONCEPTO;
		private Number TOTAL;
		private Number PAGOS;
		private Number DISPONIBLE;
		private String BANCO;
		private String DESCRIPCION;
		public String getTIPO() {
			return TIPO;
		}
		public void setTIPO(String tipo) {
			TIPO = tipo;
		}
		public Date getFECHA() {
			return FECHA;
		}
		public void setFECHA(Date fecha) {
			FECHA = fecha;
		}
		public String getSUCURSAL() {
			return SUCURSAL;
		}
		public void setSUCURSAL(String sucursal) {
			SUCURSAL = sucursal;
		}
		public String getCLAVE() {
			return CLAVE;
		}
		public void setCLAVE(String clave) {
			CLAVE = clave;
		}
		public String getNOMBRE() {
			return NOMBRE;
		}
		public void setNOMBRE(String nombre) {
			NOMBRE = nombre;
		}
		public String getORIGEN() {
			return ORIGEN;
		}
		public void setORIGEN(String origen) {
			ORIGEN = origen;
		}
		public String getCONCEPTO() {
			return CONCEPTO;
		}
		public void setCONCEPTO(String concepto) {
			CONCEPTO = concepto;
		}
		
		public Number getTOTAL() {
			return TOTAL;
		}
		public void setTOTAL(Number total) {
			TOTAL = total;
		}
		public Number getPAGOS() {
			return PAGOS;
		}
		public void setPAGOS(Number pagos) {
			PAGOS = pagos;
		}
		public Number getDISPONIBLE() {
			return DISPONIBLE;
		}
		public void setDISPONIBLE(Number disponible) {
			DISPONIBLE = disponible;
		}
		public String getBANCO() {
			return BANCO;
		}
		public void setBANCO(String banco) {
			BANCO = banco;
		}
		public String getDESCRIPCION() {
			return DESCRIPCION;
		}
		public void setDESCRIPCION(String descripcion) {
			DESCRIPCION = descripcion;
		}
		
		
		
	}
	
	public static void show(String dateAsString,final String origen){
		Date fecha=DateUtil.toDate(dateAsString);
		show(fecha, origen);
	}
	
	
	public static void show(final Date fecha,final String origen){
		final AnalisisSaldosAFavorPanel browser=new AnalisisSaldosAFavorPanel();
		browser.setFecha(fecha);
		final FilterBrowserDialog dialog=new FilterBrowserDialog(browser);
		dialog.setModal(false);
		dialog.open();
	}
	
	/**
	 * Prueba local en el EDT
	 * 
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		
		
		
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				DBUtils.whereWeAre();
				com.luxsoft.siipap.swing.utils.SWExtUIManager.setup();
				show("01/06/2011", "CAM");
				//System.exit(0);
			}

		});
	}

}
