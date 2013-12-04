package com.luxsoft.sw3.cxp.selectores;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.ActionLabel;
import com.luxsoft.siipap.cxp.model.CXPFactura;
import com.luxsoft.siipap.model.Periodo;
import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.dialog.AbstractSelector;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;


/**
 * Selector de instancias de {@link CXPFactura} 
 * 
 * @author Ruben Cancino 
 *
 */
public  class SelectorDeFacturasDeCompras extends AbstractSelector<CXPFactura>{
	
	
	
	protected Periodo periodo=Periodo.getPeriodoDelMesActual();
	
	public SelectorDeFacturasDeCompras() {
		super(CXPFactura.class, "Facturas");
		
	}
	
	protected JTextField docField=new JTextField(10);
	
	@Override
	protected void installEditors(EventList<MatcherEditor<CXPFactura>> editors) {
		textFilter=new JTextField(10);
		TextComponentMatcherEditor docEditor=new TextComponentMatcherEditor(docField,GlazedLists.textFilterator(new String[]{"documento"}));
		editors.add(docEditor);
	}

	protected JComponent buildFilterPanel(){
		ButtonBarBuilder builder=ButtonBarBuilder.createLeftToRightBuilder();		
		builder.addUnrelatedGap();
		
		ActionLabel al=new ActionLabel("Cambiar periodo");
		al.addActionListener(EventHandler.create(ActionListener.class, this, "cambiarPeriodo"));
		builder.addGridded(al);
		builder.addRelatedGap();
		
		builder.addGridded(new JLabel("Factura"));
		builder.addRelatedGap();
		builder.addGridded(docField);
		builder.addGlue();
		return builder.getPanel();
	}

	@Override
	protected TableFormat<CXPFactura> getTableFormat() {
		String props[]={"id","documento","fecha","vencimiento","moneda","total","pagos","saldoCalculado","requisitado","analizadoComoHojeo"};
		String labels[]={"Id","Docto","Fecha","Vencimiento","Moneda","Total","Pagos","Saldo","Requisitado","Analizado Hojeo"};
		return GlazedLists.tableFormat(CXPFactura.class,props,labels);
	}
	
	private HeaderPanel header=new HeaderPanel("","");
	
	protected JComponent buildHeader(){
		updateHeader();
		return header;
	}
	
	protected void setPreferedDimension(JComponent gridComponent){
		gridComponent.setPreferredSize(new Dimension(810,500));
	}
	
	public void cambiarPeriodo(){
		ValueHolder holder=new ValueHolder(periodo);
		AbstractDialog dialog=Binder.createPeriodoSelector(holder);
		dialog.open();
		this.periodo=(Periodo)holder.getValue();
		updateHeader();
		load();
	}
	
	protected JComponent buildToolbar(){
		final JToolBar bar=new JToolBar();
		ToolBarBuilder builder=new ToolBarBuilder(bar);
		Action a=CommandUtils.createLoadAction(this, "load");
		a.putValue(Action.NAME, "Buscar");
		a.putValue(Action.SHORT_DESCRIPTION, "Buscar facturas en otro periodo");
		builder.add(a);		
		builder.add(buildFilterPanel());		
		return builder.getToolBar();
	}

	protected void updateHeader(){
		header.setDescription("Cuentas por pagar   ");
		header.setTitle(MessageFormat.format("Periodo del:  {0,date,medium} al: {1,date,medium}",periodo.getFechaInicial(),periodo.getFechaFinal()));
	}

	@Override
	protected List<CXPFactura> getData() {
		String hql="from CXPFactura f where f.fecha between ? and ?";
		return ServiceLocator2.getHibernateTemplate().find(hql,new Object[]{periodo.getFechaInicial(),periodo.getFechaFinal()});
	}
	
	public void clean(){
		source.clear();
	}	
	
	public static CXPFactura buscar(){
		List<CXPFactura> facturas=new ArrayList<CXPFactura>();
		SelectorDeFacturasDeCompras selector=new SelectorDeFacturasDeCompras();
		selector.setSelectionMode(ListSelection.SINGLE_SELECTION);
		selector.open();
		if(!selector.hasBeenCanceled()){
			facturas.addAll(selector.getSelectedList());
		}		
		return facturas.isEmpty()?null:facturas.get(0);
		
	}
	
	
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		SWExtUIManager.setup();
		SwingUtilities.invokeAndWait(new Runnable(){
			 
			public void run() {
				buscar();
				System.exit(0);
			}
			
		});
		
	}

}
