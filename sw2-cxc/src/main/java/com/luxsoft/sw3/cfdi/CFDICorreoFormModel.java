package com.luxsoft.sw3.cfdi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.validator.Email;
import org.hibernate.validator.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import com.luxsoft.luxor.utils.Bean;
import com.luxsoft.siipap.cxc.model.NotaDeCargo;
import com.luxsoft.siipap.cxc.model.NotaDeCredito;
import com.luxsoft.siipap.cxc.ui.CXCUIServiceFacade;
import com.luxsoft.siipap.model.core.Cliente;
import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;



import com.luxsoft.siipap.util.DateUtil;
import com.luxsoft.siipap.ventas.model.Venta;
import com.luxsoft.sw3.cfdi.model.CFDI;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


public class CFDICorreoFormModel extends DefaultFormModel{
	
	private List<CFDI> cfds;
	
	public static String DEFAULT_CC1="credito@papelsa.com.mx";

	public CFDICorreoFormModel() {
		this(ListUtils.EMPTY_LIST);
		
	}
	
	public CFDICorreoFormModel(List<CFDI> cfds) {
		super(Bean.proxy(CorreoModel.class));
		this.cfds=cfds;
		setValue("from", DEFAULT_CC1);
		getModel("cliente").addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				Cliente c=(Cliente)arg0.getNewValue();
				if(c!=null){
					String res=StringUtils.trim(ServiceLocator2.getCFDIMailServices().localizarEmail(c));
					getCorreo().setTo(res);
				}
					
				else
					getCorreo().setTo(null);
			}
		});
	}
	
	public CorreoModel getCorreo(){
		return (CorreoModel)getBaseBean();
	}
	
	private String htmlText;
	
	public String getHtml() {
		if(htmlText==null){
			Configuration cfg=new Configuration();
			cfg.setDateFormat("dd/MM/yyyy");
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setClassForTemplateLoading(getClass(), "/");
			final String nombre=getCorreo().getCliente().getNombre();
			String path="images2/papelLogo.jpg";
			ClassPathResource resource=new ClassPathResource(path);
			Map root=new HashMap();
			root.put("cliente", nombre);
			root.put("cfds", cfds);
			root.put("fecha", new Date());
			root.put("remitenteMail",DEFAULT_CC1 );
			root.put("cid:papelLogo", resource);
			root.put("empresa",ServiceLocator2.getConfiguracion().getSucursal().getEmpresa().getNombre());
			try {
				String cfdiCorreoTemplate=System.getProperty("cfdi.correo.template.path", "templates/correoDeCFDI.ftl");
				final Template temp=cfg.getTemplate(cfdiCorreoTemplate);			
				StringWriter out=new StringWriter();
				temp.process(root, out);			
				htmlText=out.toString();
			} catch (Exception e) {
				logger.error(e);
				htmlText="Error en template: "+ExceptionUtils.getRootCauseMessage(e);
			}
		}
		return htmlText;
					
	}
	
	public void commit(){
		try {
			MimeMessageHelper msg=prepararCorreoElectronico(
					getCorreo().getFrom()
					,getCorreo().getTo()
					, cfds.toArray(new CFDI[0]));
			
			
			if(StringUtils.isNotEmpty(getCorreo().getCc1())){
				msg.setBcc(getCorreo().getCc1());
			}
			if(StringUtils.isNotEmpty(getCorreo().getCc2())){
				msg.setBcc(getCorreo().getCc2());
			}
			if(StringUtils.isNotEmpty(getCorreo().getCc3())){
				msg.setBcc(getCorreo().getCc3());
			}
			msg.setSubject("Comprobantes fiscales digitales");
			msg.setText(getHtml(),true);
			//msg.addInline("cid:papelLogo", resource);
			
			ServiceLocator2.getCFDIMailServices().getMailSender().send(msg.getMimeMessage());
			System.out.println("Correo enviado...");
			for(CFDI cfdi:cfds){
				cfdi.setComentario("Enviado: "+DateUtil.getDateTime("dd/MM/yyyy hh:mm:ss",new Date()));
				ServiceLocator2.getHibernateTemplate().merge(cfdi);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(ExceptionUtils.getRootCauseMessage(e),e);
		}
	}
	
	public MimeMessageHelper prepararCorreoElectronico(
			String from
			,String to
			,CFDI...cfds ){
		Assert.notEmpty(cfds,"La lista de CFDIs a enviar esta vacia");
		
		try {
			MimeMessage mimeMessage=ServiceLocator2.getCFDMailServices().getMailSender().createMimeMessage();
			MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage,true);
			messageHelper.setFrom(from);
			messageHelper.setTo(to);
			messageHelper.setSubject("");
			messageHelper.setText("");
			for(final CFDI cfd:cfds){
				try {
					//XML
					byte[] xml=cfd.getXml();
					InputStreamSource source=new ByteArrayResource(xml);
					
					//messageHelper.addAttachment(cfd.getXmlFilePath(),source);
					String receptor=StringUtils.replace(cfd.getReceptor(),".","");
				    
					int indice=receptor.indexOf(",");
				   
				   if (indice==-1){
					   indice=receptor.length();
				   }
					   
				   System.out.println("**************************"+indice);
				//String xmlName=receptor.substring(0, indice).concat("-").concat(cfd.getFolio());
				   String xmlName=new String("PAPEL S A -").concat(cfd.getFolio());
					messageHelper.addAttachment(xmlName+".xml",source);
					
					//PDF
				//	if(cfd.getTipo().equals("FACTURA")){
						
						JasperPrint jp=null;
						
						if(cfd.getTipo().equals("FACTURA")){
							jp=(JasperPrint)ServiceLocator2.getHibernateTemplate().execute(new HibernateCallback() {
								public Object doInHibernate(Session session) throws HibernateException,SQLException {
									Venta venta=(Venta)session.get(Venta.class, cfd.getOrigen());
									System.err.println("enviando la fac" + venta);
									JasperPrint jp=CFDIPrintServices.impripirComprobante(venta, cfd, "", false);
									return jp;
								}
							});
						}else if(cfd.getTipo().equals("NOTA_CREDITO")){
							NotaDeCredito nota=CXCUIServiceFacade.buscarNotaDeCreditoInicializada(cfd.getOrigen());
							System.err.println("enviando la nota Credito" + nota);
							 jp=CFDINotaPrintServices.impripirComprobante(nota, cfd);
						}else if(cfd.getTipo().equals("NOTA_CARGO")){
							NotaDeCargo nota=CXCUIServiceFacade.buscarNotaDeCargoInicializada(cfd.getOrigen());
							System.err.println("enviando la nota Cargo" + nota);
							jp=CFDINotaDeCargoPrintServices.impripirComprobante(nota, cfd);
						} 
							
						
						if(jp!=null){
							byte[] pdf=JasperExportManager.exportReportToPdf(jp);
							InputStreamSource sourcePdf=new ByteArrayResource(pdf);
							//String pdfName=StringUtils.replace(cfd.getXmlFilePath(), ".xml", ".pdf");
							//String pdfName=receptor.substring(0, indice).concat("-").concat(cfd.getFolio());
							String pdfName=new String("PAPEL S A-").concat(cfd.getFolio());
							
							//messageHelper.addAttachment(pdfName,sourcePdf);
							messageHelper.addAttachment(pdfName+".pdf",sourcePdf);
						}
						
					//}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return messageHelper;
		} catch (Exception e) {
			String msg="Error preparando correo: \n"+ExceptionUtils.getRootCauseMessage(e);
			throw new RuntimeException(msg,e);
		}
		
	}
	
	
	
	public static class CorreoModel {
		
		private Cliente cliente;
		
		private URL templateUrl;
		
		@Email
		@NotNull(message="Digite el Remitente")
		private String from;
		
		@Email
		@NotNull(message="Digite el correo destino")
		private String to;
		
		@Email
		private String cc1;
		
		@Email
		private String cc2;
		
		@Email
		private String cc3;
		
		public Cliente getCliente() {
			return cliente;
		}
		public void setCliente(Cliente cliente) {
			this.cliente = cliente;
			//setTo(cliente!=null?cliente.getEmai3():null);
		}
		public URL getTemplateUrl() {
			return templateUrl;
		}
		public void setTemplateUrl(URL templateUrl) {
			this.templateUrl = templateUrl;
		}
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
		public String getCc1() {
			return cc1;
		}
		public void setCc1(String cc1) {
			this.cc1 = cc1;
		}
		public String getCc2() {
			return cc2;
		}
		public void setCc2(String cc2) {
			this.cc2 = cc2;
		}
		public String getCc3() {
			return cc3;
		}
		public void setCc3(String cc3) {
			this.cc3 = cc3;
		}
		public String getClienteNombre(){
			return getCliente().getNombreRazon();
		}
		
		
	}

}
