package com.luxsoft.siipap.cxc.ui.consultas;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.luxsoft.siipap.cxc.ui.selectores.SelectorDeClientes;
import com.luxsoft.siipap.model.Direccion;
import com.luxsoft.siipap.model.core.Cliente;
import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.browser.FilteredBrowserPanel;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.sw3.cfd.model.ComprobanteFiscal;
import com.luxsoft.sw3.cfd.task.GeneradorDePdf;
import com.luxsoft.sw3.mail.CorreoForm;



public class CFDPanel extends FilteredBrowserPanel<ComprobanteFiscal>{
	
	private Cliente cliente;

	public CFDPanel() {
		super(ComprobanteFiscal.class);
		setTitle("Comprobantes fiscales digitales (CFD)");		
	}
	
	public void init(){
		addProperty(
				"serie","tipo","folio","log.creado","total","estado"
				);
		addLabels(
				"Serie","Tipo","Folio","Fecha","Total","Estado"
				);
		installTextComponentMatcherEditor("Serie", "serie");
		installTextComponentMatcherEditor("Folio", "folio");
		installTextComponentMatcherEditor("Tipo", "tipo");
		installTextComponentMatcherEditor("Total", "total");
		manejarPeriodo();
	}
	
	@Override
	protected void afterGridCreated() {		
		super.afterGridCreated();
		JPopupMenu popup=new JPopupMenu("Operaciones");
		for(Action a:getActions()){
			popup.add(a);
		}
		getGrid().setComponentPopupMenu(popup);
	}
	
	protected void beforeLoad(){
		super.beforeLoad();
		seleccionarCliente();
	}

	@Override
	protected List<ComprobanteFiscal> findData() {
		if(getCliente()==null)
			return ListUtils.EMPTY_LIST;
		String hql="from ComprobanteFiscal c where date(c.log.creado) " +
				"between ? and ? and c.rfc=? " +
				" and c.origen not in(select cc.cargo.id from CancelacionDeCargo cc)";
		return ServiceLocator2.getHibernateTemplate().find(hql
				, new Object[]{periodo.getFechaInicial()
				,periodo.getFechaFinal(),getCliente().getRfc()}
				);
	}
	
	public void seleccionarCliente(){
		cliente=SelectorDeClientes.seleccionar();
		updateHeader(cliente);
	}

	private Header header;
	
	@Override
	protected JComponent buildHeader() {
		header = new Header("Seleccione un cliente", "");
		return header.getHeader();
	}
	
	public void updateHeader(Cliente c) {
		
		if (c != null) {
			
			header.setTitulo(c.getNombreRazon()+ " ( "+c.getClave()+" )");
			if (c.getDireccionFiscal() != null) {
				String pattern = "" +
						"Calle  : {0}  #       {1} Int  : {7} 		Cr�dito{2} \n"+
						"Col    : {3} CP:      {4} \n" +
						"Del/Mpo: {5} Entidad: {6} 	Tel(s):{8}  {9}	";
				Direccion df = c.getDireccionFiscal();
				String msg = MessageFormat.format(pattern, 
						df.getCalle(), 
						df.getNumero(),
						c.isDeCredito() ? (c.getCredito().isSuspendido() ? "NO": "SI") : "NO",
						df.getColonia(),
						df.getCp(), 
						df.getMunicipio(),
						df.getEstado()
						,StringUtils.trimToEmpty(df.getNumeroInterior())
						,StringUtils.trimToEmpty(c.getTelefono1())
						,c.getTelefono2()
							);
				header.setDescripcion(msg);
			}

		} else {
			header.setTitulo("Seleccione un cliente");
			header.setDescripcion("");
		}
	}
	
	@Override
	protected List<Action> createProccessActions(){
		List<Action> res=super.createProccessActions();
		res.add(addAction(null, "mandarPorCorreoElectronico", "Mandar por Correo"));
		return res;
	}

	@Override
	protected void doSelect(Object bean) {
		ComprobanteFiscal cfd=(ComprobanteFiscal)bean;
		if(cfd.getTipo().equals("FACTURA")){
			FacturaForm.show(cfd.getOrigen());
		}
		
	}

	public Cliente getCliente() {
		return cliente;
	}
	
	private GeneradorDePdf generador=new GeneradorDePdf();

	public void mandarPorCorreoElectronico(){
		if(!getSelected().isEmpty()){			
			EventList<ComprobanteFiscal> cfds=GlazedLists.eventList(getSelected());
			for(ComprobanteFiscal cfdi:cfds){
				try {
					String path="file:/"+System.getProperty("cfd.dir.path")+"/"+cfdi.getXmlPath();
					if(cfdi.getXmlPath().startsWith("file")){
						path=cfdi.getXmlPath();
					}
					String pdfURL=StringUtils.replace( path, ".xml", ".pdf");
					
					URL pdfUrl=new URL(pdfURL);
					File pdf=new File(pdfUrl.toURI());
					
					if(!pdf.exists()){
						pdf=generador.generarPdfFile(cfdi);
						System.out.println("PDF generado: "+pdf.getPath());
					}
					//System.out.println("Mandar");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CorreoForm.mandarCorreo(getCliente(), cfds);
		}
		
	}
	
	
	

}
