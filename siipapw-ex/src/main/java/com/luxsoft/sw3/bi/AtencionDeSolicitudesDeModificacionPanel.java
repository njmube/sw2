package com.luxsoft.sw3.bi;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;

import org.jdesktop.swingx.JXTable;
import org.jfree.ui.DateCellRenderer;

import com.luxsoft.siipap.cxc.CXCRoles;
import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.browser.FilteredBrowserPanel;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.swing.utils.Renderers.ToHourConverter;
import com.luxsoft.sw3.solicitudes.SolicitudDeModificacion;
import com.luxsoft.sw3.solicitudes.SolicitudDeModificacionForm;
import com.luxsoft.sw3.solicitudes.SolicitudDeModificacionFormModel;




/**
 * Consulta para la atencion de solicitud de modificaciones
 *  
 * @author Ruben Cancino 
 *
 */
public class AtencionDeSolicitudesDeModificacionPanel extends FilteredBrowserPanel<SolicitudDeModificacion>{

	public AtencionDeSolicitudesDeModificacionPanel() {
		super(SolicitudDeModificacion.class);		
	}
	
	protected void init(){
		String[] props={"sucursal","folio","fecha","modulo","tipo","documento","estado","usuario","log.creado","autorizo","autorizacion","atendio","log.modificado","descripcion","comentarioDeAtencion"};
		addProperty(props);
		addLabels("Sucursal","Folio","Fecha","Modulo","tipo","documento","estado","Solicito","Solicitado","Autorizo","Autorizado","Atendio","Atendido","Descripcion","C.Atendio");
		installTextComponentMatcherEditor("Sucursal", "sucursal.nombre");
		installTextComponentMatcherEditor("Usuario", "usuario");
		installTextComponentMatcherEditor("Documento", "documento");
		installTextComponentMatcherEditor("Estado", "estado");
		installTextComponentMatcherEditor("Fecha", "fecha");
		manejarPeriodo();
	}
	
	

	@Override
	public Action[] getActions() {
		if(actions==null){
			actions=new Action[]{
				getLoadAction()
				,getViewAction()
				,addRoleBasedAction(CXCRoles.AUTORIZAR_MODIFICACIONES_DE_DATOS.name(),"atender", "Atender")
				,addRoleBasedAction(CXCRoles.AUTORIZAR_MODIFICACIONES_DE_DATOS.name(),"reporte", "Reporte")
				};
		}
		return actions;
	}

	@Override
	protected List<SolicitudDeModificacion> findData() {
		String hql="from SolicitudDeModificacion s where s.fecha between  ? and ? order by s.estado ";
		return ServiceLocator2.getHibernateTemplate().find(hql,new Object[]{periodo.getFechaInicial(),periodo.getFechaFinal()});	
	}

	@Override
	public void open() {
		//load();
		timer=new Timer();
		timer.schedule(task, 1000, 30000);
	}
	
	private Timer timer;
	
	TimerTask task=new TimerTask() {
		@Override
		public void run() {
			//System.out.println("Cargando datos en timer......");
			load();
		}
	};
	
	@Override
	public void close() {
		super.close();
		//System.out.println("Cancelando tarea de cargado en background..");
		task.cancel();
		timer.purge();
	}
	

	@Override
	protected void adjustMainGrid(JXTable grid) {

		grid.getColumnExt("Solicitado").setCellRenderer(new DateCellRenderer(new SimpleDateFormat("dd/MM/yyyy :hh:mm")));
		grid.getColumnExt("Autorizado").setCellRenderer(new DateCellRenderer(new SimpleDateFormat("dd/MM/yyyy :hh:mm")));
		grid.getColumnExt("Atendido").setCellRenderer(new DateCellRenderer(new SimpleDateFormat("dd/MM/yyyy :hh:mm")));
		
		
		//grid.getColumnExt("Recibi�").setVisible(false);
		//grid.getColumnExt("Surtidor").setVisible(false);
		
	}
	
	public void atender(){
		SolicitudDeModificacion row=(SolicitudDeModificacion)getSelectedObject();
		if(row!=null){
			final AtenecionDeModificacionForm form=new AtenecionDeModificacionForm();
			form.open();
			if(!form.hasBeenCanceled()){
				row.setAtendio(form.getModificacion().getAtendio());
				row.setComentarioDeAtencion(form.getModificacion().getComentarioDeAtencion());
				row.setEstado(form.getModificacion().getEstado());
				ServiceLocator2.getHibernateTemplate().merge(row);
				load();
			}
		}
	}
	
		
	public void reporte(){
		//BitacoraClientesCredito.show();
	}
	
	@Override
	protected void doSelect(Object bean) {
		SolicitudDeModificacion selected=(SolicitudDeModificacion)bean;
		SolicitudDeModificacionFormModel model=new SolicitudDeModificacionFormModel(selected, selected.getSucursal());
		model.setReadOnly(true);
		model.setManager(ServiceLocator2.getSolicitudDeModificacionesManager());
		SolicitudDeModificacionForm form=new SolicitudDeModificacionForm(model);
		form.setHibernateTemplate(ServiceLocator2.getHibernateTemplate());
		form.open();
	}
	

}
