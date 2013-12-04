package com.luxsoft.sw3.cxp.forms;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.TableUtilities;
import com.luxsoft.siipap.inventarios.model.TransformacionDet;
import com.luxsoft.siipap.model.core.Proveedor;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.binding.Bindings;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.ResourcesUtils;



/**
 * Forma para la mantenimiento de gastos de flete 
 * de maquila al Almacen de ventas
 * 
 * @author Ruben Cancino Ramos
 *
 */
public class AnalisisDeTransformacionForm extends AbstractForm implements ListSelectionListener{
	
	

	public AnalisisDeTransformacionForm(final AnalisisDeTransformacionFormModel model) {
		super(model);
		setTitle("An�lisis de TRS");
		if(model.getValue("id")==null){
			model.getModel("proveedor").addValueChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					updateHeader();
				}
			});
		}
	}
	
	public AnalisisDeTransformacionFormModel getController(){
		return (AnalisisDeTransformacionFormModel)getModel();
	}	

	@Override
	protected JComponent buildFormPanel() {
		JPanel panel=new JPanel(new VerticalLayout(5));
		
		FormLayout layout=new FormLayout(
				"p,2dlu,80dlu,3dlu," +
				"p,2dlu,80dlu,3dlu," +
				"p,2dlu,80dlu:g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		if(model.getValue("id")==null){
			builder.append("Proveedor",getControl("proveedor"),9);
		}
		else
			builder.append("Id",addReadOnly("id"),true);
		
		builder.append("Fecha",getControl("fecha"));
		builder.nextLine();
		builder.append("Factura",getControl("factura"));
		builder.append("F.Factura",getControl("fechaFactura"));
		builder.append("Descuento",getControl("descuento"));
		builder.nextLine();
		
		builder.append("Importe",getControl("importe"));
		JFormattedTextField tf=(JFormattedTextField)getControl("importe");
		tf.addPropertyChangeListener("value", new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				getControl("comentario").requestFocus();
			}
		});
		
		builder.append("Impuesto",getControl("impuesto"));
		builder.nextLine();
		builder.append("Retencion",addReadOnly("retencion"));
		builder.append("Total",getControl("total"));
		builder.nextLine();
		builder.append("Comentario",getControl("comentario"),9);
		
		panel.add(builder.getPanel());		
		panel.add(buildGridPanel());
		panel.add(buildToolbarPanel());
		ajustarActions(panel);
		
		
		return panel;
	}
	
	private Header header;
	
	@Override
	protected JComponent buildHeader() {
		if(header==null){
			header=new Header("Seleccione un Proveedor","");
			updateHeader();
		}
		return header.getHeader();
	}
	
	private void updateHeader(){
		Proveedor p=(Proveedor)model.getValue("proveedor");
		if(p!=null){
			header.setTitulo(p.getNombreRazon());
			header.setDescripcion("Direcci�n: "+p.getDireccion().toString());
		}else{
			header.setTitulo("Seleccione un proveedor");
			header.setDescripcion("");
		}
	}
	
	protected void ajustarActions(JPanel panel){
		getOKAction().putValue(Action.NAME, "Salvar [F10]");
		getOKAction().putValue(Action.SMALL_ICON, ResourcesUtils.getIconFromResource("images/edit/save_edit.gif"));
		getCancelAction().putValue(Action.NAME, "Cancelar");
		ComponentUtils.addAction(panel, new AbstractAction(){			
			public void actionPerformed(ActionEvent e) {
				if(getOKAction().isEnabled())
					getOKAction().actionPerformed(null);
			}
		}, 
		KeyStroke.getKeyStroke("F10"), JComponent.WHEN_IN_FOCUSED_WINDOW);
		ComponentUtils.addInsertAction(panel, getInsertAction());
	}
	
	
	
	@Override
	protected void onWindowOpened() {
		super.onWindowOpened();
		if(model.getValue("id")!=null)
			getControl("factura").requestFocusInWindow();
		else
			getControl("proveedor").requestFocusInWindow();
	}

	protected JComponent createNewComponent(final String property){
		JComponent c=super.createNewComponent(property);
		c.setEnabled(!model.isReadOnly());
		return c;
	}
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if("comentario".equals(property)){
			JComponent control=Binder.createMayusculasTextField(model.getModel(property), false);
			control.setEnabled(!model.isReadOnly());
			return control;			
		}else if("proveedor".equals(property)){
			return buildProveedorControl(model.getModel(property));
		}else if("descuento".equals(property)){
			JComponent control=Bindings.createDescuentoEstandarBinding(model.getModel(property));
			control.setEnabled(!model.isReadOnly());
			return control;			
		}
		return null;
	}
	
	private JComponent buildProveedorControl(final ValueModel vm) {
		if (model.getValue("id") == null) {
			final JComboBox box = new JComboBox();
			final EventList source = GlazedLists.eventList(getController().getProveedores());
			final TextFilterator filterator = GlazedLists.textFilterator(new String[] { "clave", "nombre", "rfc" });
			AutoCompleteSupport support = AutoCompleteSupport.install(box,source, filterator);
			support.setFilterMode(TextMatcherEditor.CONTAINS);
			support.setStrict(false);
			final EventComboBoxModel model = (EventComboBoxModel) box.getModel();
			model.addListDataListener(new Bindings.WeakListDataListener(vm));
			box.setSelectedItem(vm.getValue());
			return box;
		} else {
			String prov = ((Proveedor) vm.getValue()).getNombreRazon();
			JLabel label = new JLabel(prov);
			return label;
		}
	}
	
	protected JPanel buildToolbarPanel(){
		
		Action com=new com.luxsoft.siipap.swing.actions.DispatchingAction(this,"insertar");		
		com.putValue(Action.NAME, "Transformaciones");
		com.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		
		getDeleteAction().putValue(Action.NAME, "Eliminar [DEL]");
		getDeleteAction().putValue(Action.MNEMONIC_KEY, KeyEvent.VK_DELETE);
		getEditAction().putValue(Action.NAME, "Editar");
		
		getInsertAction().setEnabled(!model.isReadOnly());
		getDeleteAction().setEnabled(!model.isReadOnly());
		getEditAction().setEnabled(!model.isReadOnly());
		
		getViewAction().setEnabled(false);
		
		JButton buttons[]={
				new JButton(com)
				,new JButton(getDeleteAction())
				
				
		};
		return ButtonBarFactory.buildGrowingBar(buttons);
	}
	
	
	
	private JXTable grid;
	
	private EventSelectionModel<TransformacionDet> selectionModel;
	
	protected JComponent buildGridPanel(){
		String[] props={
				"sucursal.nombre"
				,"documento"
				,"fecha"
				,"producto.clave"
				,"producto.descripcion"
				,"cantidad"
				,"kilosCalculados"
				,"gastos"
				,"importeGasto"
				};
		String[] names={
				"Sucursal"
				,"Documento"
				,"Fecha"
				,"Producto"
				,"Descripcion"
				,"Cantidad"
				,"Kilos"
				,"Costo Met"
				,"Importe Met"
				};
		 
		final TableFormat tf=GlazedLists.tableFormat(TransformacionDet.class,props,names);
		final EventTableModel tm=new EventTableModel(getController().getPartidasSource(),tf);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		selectionModel=new EventSelectionModel<TransformacionDet>(getController().getPartidasSource());
		selectionModel.addListSelectionListener(this);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addInsertAction(grid,getInsertAction());
		ComponentUtils.addDeleteAction(grid, getDeleteAction());
		
		grid.setEnabled(!model.isReadOnly());
		JComponent gridComponent=ComponentUtils.createTablePanel(grid);		
		gridComponent.setPreferredSize(new Dimension(790,300));
		grid.packAll();
		return gridComponent;
		
	}
	
	public void insertar(){
		getController().insertarTransformaciones();
		TableUtilities.resizeColumnsToPreferredWidth(grid);
		grid.requestFocusInWindow();
		grid.packAll();
	}
	
	public void deletePartida(){
		if(!selectionModel.isSelectionEmpty()){
			for(int index=selectionModel.getMinSelectionIndex();index<=selectionModel.getMaxSelectionIndex();index++){
				getController().elminarPartida(index);
			}
		}
	}
	
	
	public void valueChanged(ListSelectionEvent e) {
		boolean val=!selectionModel.isSelectionEmpty();
		if(model.isReadOnly()){
			val=false;
		}
		getEditAction().setEnabled(val);
		getDeleteAction().setEnabled(val);
		getViewAction().setEnabled(!selectionModel.isSelectionEmpty());
		
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
				com.luxsoft.siipap.swing.utils.SWExtUIManager.setup();
				AnalisisDeTransformacionFormModel controller=new AnalisisDeTransformacionFormModel();
				AnalisisDeTransformacionForm form=new AnalisisDeTransformacionForm(controller);
				form.open();
				if(!form.hasBeenCanceled()){
					System.out.println(ToStringBuilder.reflectionToString(controller.getBaseBean()));
					
				}
				System.exit(0);
			}

		});
	}

		

}
