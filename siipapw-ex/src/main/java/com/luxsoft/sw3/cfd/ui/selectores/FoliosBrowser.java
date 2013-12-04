package com.luxsoft.sw3.cfd.ui.selectores;

import javax.swing.Action;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;



import com.luxsoft.siipap.service.ServiceLocator2;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.dialog.UniversalAbstractCatalogDialog;
import com.luxsoft.sw3.cfd.model.FolioFiscal;
import com.luxsoft.sw3.cfd.ui.form.FolioForm;




/**
 *  Browser para el mantenimiento de entidades de tipo {@link FolioFiscal}
 * 
 * @author Ruben Cancino
 *
 */
public class FoliosBrowser extends UniversalAbstractCatalogDialog<FolioFiscal>{
	
	private static Action showAction;

	public FoliosBrowser() {
		super(FolioFiscal.class,new BasicEventList<FolioFiscal>(), "Folios fiscales ");
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.swing.dialog.UniversalAbstractCatalogDialog#getTableFormat()
	 */
	@Override
	protected TableFormat<FolioFiscal> getTableFormat() {
		final String[] cols={"id.sucursal","id.serie","folio","noAprobacion","anoAprobacion","asignacion","folioInicial","folioFinal"};
		final String[] names={"Suc","Serie","Folio","No aprob","A�o aprob","Asignacion","Ini","Final"};
		return GlazedLists.tableFormat(FolioFiscal.class, cols,names);
	}
	
	/**** Personalizacion de comportamiento (A/B/C) ****/
	
	@Override
	protected FolioFiscal doInsert() {		
		FolioFiscal folio=FolioForm.showForm();
		if(folio!=null){
			folio=(FolioFiscal)ServiceLocator2.getUniversalDao().save(folio);
			return folio;
		}
		return null;
	}
	
	@Override
	protected FolioFiscal doEdit(FolioFiscal bean) {		
		/*Linea res=LineaForm.showForm(bean);
		if(res!=null)
			return save(res);
			*/
		return null;
	}
	
	protected void doView(FolioFiscal bean){
		//LineaForm.showForm(bean,true);
	}
	
	
	/**** Fin Personalizacion de comportamiento****/

	/**
	 * Acceso a una Action que permite mostrar este browser.	 * 
	 * Patron FactoryMethod para se usado desde  Spring
	 * Existe solo para facilitar el uso en Spring
	 * 
	 * @return
	 */
	public static Action getShowAction(){		
		showAction=new SWXAction(){
				@Override
				protected void execute() {
					openDialog();
				}				
			};		
		return showAction;
	}	
	
	public static void openDialog(){
		FoliosBrowser dialog=new FoliosBrowser();
		dialog.open();
	}
	

	public static void main(String[] args) {
		openDialog();
		System.exit(0);
		
	}

}
