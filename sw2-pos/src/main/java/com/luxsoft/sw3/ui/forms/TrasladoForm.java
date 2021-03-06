package com.luxsoft.sw3.ui.forms;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
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

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.inventarios.model.SolicitudDeTraslado;
import com.luxsoft.siipap.inventarios.model.SolicitudDeTrasladoDet;
import com.luxsoft.siipap.pos.ui.utils.UIUtils;
import com.luxsoft.siipap.swing.binding.Bindings;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.NumberCellEditor;
import com.luxsoft.siipap.swing.utils.ResourcesUtils;
import com.luxsoft.sw3.embarque.Chofer;
import com.luxsoft.sw3.services.Services;


/**
 * Forma para la atencion de traslados
 * 
 * @author Ruben Cancino Ramos
 *
 */
public class TrasladoForm extends AbstractForm {

	public TrasladoForm(final TrasladoController model) {
		super(model);
		setTitle("Generacin de Traslados         ("+model.getValue("sucursal")+" )");
		getController().getUserModel().addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				String user=getController().getUser();
				usuarioNombre.setText(StringUtils.defaultString(user));
			}
			
		});
	}
	
	public TrasladoController getController(){
		return (TrasladoController)getModel();
	}
	
	private JLabel usuarioNombre;

	@Override
	protected JComponent buildFormPanel() {
		
		JPanel panel=new JPanel(new VerticalLayout(5));
		
		FormLayout layout=new FormLayout(
				"p,2dlu,max(90dlu;p),3dlu," +
				"p,2dlu,max(90dlu;p),3dlu," +
				"p,2dlu,max(90dlu;p):g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.appendSeparator("Solicitante");
		builder.append("Sucursal",getControl("sucursal"));
		builder.append("Documento",addReadOnly("documento"));
		builder.nextLine();
		builder.append("Fecha",addReadOnly("fecha"));
		builder.append("Chofer",getControl("chofer"));
		builder.nextLine();
		builder.append("Comentario SOL",addReadOnly("comentario"),9);
		builder.append("Referencia ",addReadOnly("referencia"));
		builder.append("Clasificacion ",addReadOnly("clasificacion"));
		builder.nextLine();
		usuarioNombre=new JLabel("");
		builder.append("Atendi",getControl("usuario"));
		builder.append(usuarioNombre,5);
		builder.nextLine();
		builder.append("Por inventario",getControl("porInventario"));
		builder.append("Comentario",getControl("comentarioTps"),5);
		
		builder.appendSeparator("Bitacora");
		builder.append("Cortador",getControl("cortador"));
		builder.append("Surtidor",getControl("surtidor"));
		builder.append("Supervisor",getControl("supervisor"));
		
		panel.add(builder.getPanel());		
		panel.add(buildGridPanel());
		ajustarActions(panel);
		ComponentUtils.decorateSpecialFocusTraversal(panel);
		return panel;
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

	protected JComponent createNewComponent(final String property){
		JComponent c=super.createNewComponent(property);
		c.setEnabled(!model.isReadOnly());
		return c;
	}
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if("sucursal".equals(property)){
			return BasicComponentFactory.createLabel(model.getModel(property), UIUtils.buildToStringFormat());
		}else if("chofer".equalsIgnoreCase(property)){
			return buildChoferControl(getController().getChoferHolder());
		}else if("usuario".equals(property)){
			JComponent c=BasicComponentFactory.createPasswordField(getController().getUserModel(),true);
			c.setEnabled(!model.isReadOnly());
			return c;
		}else if("cortador".equalsIgnoreCase(property)){
			return buildOperadoresControl(getController().getCortadorHolder());
		}else if("surtidor".equalsIgnoreCase(property)){
			return buildOperadoresControl(getController().getSurtidorHolder());
		}else if("supervisor".equalsIgnoreCase(property)){
			return buildOperadoresControl(getController().getSupervisorHolder());
		}
		return null;
	}
	
	private JComponent buildOperadoresControl(final ValueModel vm){
		List<String> data=Services.getInstance().getJdbcTemplate()
				.queryForList("select concat(first_name,' ',last_name) as nombre from sx_usuarios where departamento=? ",new Object[]{"INVENTARIOS"},String.class);
	
	
		final JComboBox box = new JComboBox();
		final EventList source = GlazedLists.eventList(data);
		AutoCompleteSupport support = AutoCompleteSupport.install(box,source);
		support.setFilterMode(TextMatcherEditor.STARTS_WITH);
		support.setSelectsTextOnFocusGain(true);
		support.setStrict(false);
		
		final EventComboBoxModel model = (EventComboBoxModel) box.getModel();
		model.addListDataListener(new Bindings.WeakListDataListener(vm));
		box.setSelectedItem(vm.getValue());
		return box;
		
	}
	
	private JComponent buildChoferControl(final ValueModel vm) {
		final JComboBox box = new JComboBox();
		final EventList source = GlazedLists.eventList(Services.getInstance().getUniversalDao().getAll(Chofer.class));
		final TextFilterator filterator = GlazedLists.textFilterator(new String[] {"nombre" });
		AutoCompleteSupport support = AutoCompleteSupport.install(box,source, filterator);
		support.setFilterMode(TextMatcherEditor.CONTAINS);
		support.setStrict(false);
		final EventComboBoxModel model = (EventComboBoxModel) box.getModel();
		model.addListDataListener(new Bindings.WeakListDataListener(vm));
		box.setSelectedItem(vm.getValue());
		return box;
	}
	
	
	private JXTable grid;
	private EventSelectionModel<SolicitudDeTrasladoDet> selectionModel;
	
	protected JComponent buildGridPanel(){
		String[] propertyNames={"producto.clave","producto.descripcion","solicitado","existencia","recibido","nuevaExistencia","comentarioTps","cortes","instruccionesDecorte"};
		String[] columnLabels={"Producto","Descripcin","Solicitado","Disponible","Por Enviar","Saldo","Comentario","Cortes","Inst de Corte"};
		boolean[] edits={false,false,false,false,true,false,true,true,true};
		final TableFormat tf=GlazedLists.tableFormat(SolicitudDeTrasladoDet.class,propertyNames, columnLabels,edits);
		final EventTableModel tm=new EventTableModel(getController().getPartidasSource(),tf);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setColumnControlVisible(false);
		selectionModel=new EventSelectionModel<SolicitudDeTrasladoDet>(getController().getPartidasSource());
		
		grid.setSelectionModel(selectionModel);
		grid.setEnabled(!model.isReadOnly());	
		grid.getColumnExt("Por Enviar").setCellEditor(new NumberCellEditor(NumberFormat.getIntegerInstance()));
		grid.packAll();
		
		JComponent gridComponent=ComponentUtils.createTablePanel(grid);
		gridComponent.setPreferredSize(new Dimension(750,300));
		return gridComponent;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				com.luxsoft.siipap.swing.utils.SWExtUIManager.setup();
				SolicitudDeTraslado sol=(SolicitudDeTraslado)Services
						.getInstance().getHibernateTemplate()
						.get(SolicitudDeTraslado.class
								,"8a8a81e9-3268382c-0132-689e06e2-0017");
				TrasladoController controller=new TrasladoController(sol);
				TrasladoForm form=new TrasladoForm(controller);
				form.open();
				if(!form.hasBeenCanceled()){
					System.out.println(
							ToStringBuilder.reflectionToString(controller.getBaseBean())
					);
					controller.persistir();
				}
				System.exit(0);
			}

		});
	}

	

}
