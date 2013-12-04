package com.luxsoft.siipap.pos.ui.venta.forms;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.sw3.services.POSDBUtils;
import com.luxsoft.sw3.services.Services;
import com.luxsoft.sw3.ventas.CheckPlusVenta;
import com.luxsoft.sw3.ventas.Pedido;

public class CheckplusVentaForm extends AbstractForm{

	public CheckplusVentaForm(CheckplusVentaFormModel model) {
		super(model);
		setTitle("Bit�cora de autorizaci�n CheckPlus");
	}
	
	public CheckplusVentaFormModel getBaseModel(){
		return (CheckplusVentaFormModel)getModel();
	}
	@Override
	protected JComponent buildHeader() {
		Header header=new Header("Autorizaci�n de CheckPlus", "Recuerde capturar los datos con toda precisi�n");
		return header.getHeader();
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout=new FormLayout(
				"p,2dlu,max(p;150dlu):g(.3), 3dlu," +
				"p,2dlu,max(p;150dlu):g(.3), 3dlu," +
				"p,2dlu,p:g(.4)","");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		if(model.getValue("id")!=null)
			builder.append("Folio",addReadOnly("id"));
		
		builder.append("Raz�n social",addReadOnly("razonSocial"),9);
		builder.append("Fecha proteccion",getControl("fechaDeProteccion"),true);
		builder.append("Banco",getControl("banco"));
		builder.append(addReadOnly("bancoNombre"),6);
		
		builder.append("N�mero de cuenta",addMandatory("numeroDeCuenta"));
		builder.append("N�mero de cheque",getControl("numeroDeCheque"));
		
		builder.nextLine();
		builder.append("Tel�fono",getControl("telefono"),true);
		builder.appendSeparator("Identificaci�n");
		builder.append("Tipo",getControl("tipoDeIdentificacion"));
		builder.append("Folio",getControl("folioDeIdentificacion"),true);
		builder.appendSeparator("Autorizaci�n");
		builder.append("Atendi�",getControl("atendioCheckPlus"));
		builder.append("Clave",getControl("claveAutorizacion"),true);
		builder.appendSeparator("Direcci�n");
		builder.append(getControl("direccion"),9);
		
		return builder.getPanel();
	}
	
	@Override
	protected JComponent createCustomComponent(String property) {
		
		if("banco".equals(property)){
			 //JComponent box=Bindings.createBancosBinding(getModel().getModel(property));
			 List bancos=Services.getInstance().getHibernateTemplate().find("from Banco order by clave");
			 SelectionInList sl=new SelectionInList(bancos,model.getModel(property));
			 JComboBox box=BasicComponentFactory.createComboBox(sl);
			 box.setEnabled(!getModel().isReadOnly());
			 return box;
		}else if("tipoDeIdentificacion".equalsIgnoreCase(property)){
			SelectionInList sl=new SelectionInList(new String[]{"IFE","PASAPORTE"},getModel().getModel(property));
			JComboBox box=BasicComponentFactory.createComboBox(sl);
			box.setEnabled(!getModel().isReadOnly());
			return box;
		}
		return null;
	}
	
	public static CheckPlusVenta showForm(Pedido pedido){
		final CheckplusVentaFormModel model=new CheckplusVentaFormModel(pedido);
		final CheckplusVentaForm form=new CheckplusVentaForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			return model.commit();
		}else
			return null;
	}
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		SWExtUIManager.setup();
		SwingUtilities.invokeAndWait(new Runnable(){
			public void run() {
				
				POSDBUtils.whereWeAre();
				Pedido pedido=Services.getInstance().getPedidosManager().get("8a8a8161-3f0b2c75-013f-0b2df367-0004");
				
				Object res=showForm(pedido);
				if(res!=null)
					showObject(res);
			}
		});
	}
}
