package com.luxsoft.sw3.contabilidad.polizas.che;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.ui.ModelMap;

import ca.odell.glazedlists.EventList;

import com.luxsoft.siipap.cxc.model.Aplicacion;
import com.luxsoft.siipap.cxc.model.Pago;
import com.luxsoft.siipap.cxc.model.PagoDeDiferencias;
import com.luxsoft.sw3.contabilidad.model.Poliza;
import com.luxsoft.sw3.contabilidad.polizas.IProcesador;
import com.luxsoft.sw3.contabilidad.polizas.PolizaDetFactory;

public class Proc_CobranzaPagosConDiferenciasChe implements IProcesador{
	
	
	public void procesar(Poliza poliza, ModelMap model) {
		
		EventList<Pago> pagos=(EventList<Pago>)model.get("pagos");
		for(Pago pago:pagos){
			if(pago instanceof PagoDeDiferencias){
				String ref1=pago.getOrigenAplicacion();
				String ref2=pago.getSucursal().getNombre();
				String asiento="COBRANZA DIFERENCIAS OG";
				BigDecimal totalAplicado=pago.getAplicado(poliza.getFecha());
		
					//Cargo a Otros gastos
					PolizaDetFactory.generarPolizaDet(poliza, "704", "OGST01", true, totalAplicado, "Ajuste automatico <$1", ref1, ref2, asiento);	
					//Abono a clientes
					PolizaDetFactory.generarPolizaDet(poliza, "113", pago.getClave(), false, totalAplicado, "Ajuste automatico <$1", ref1, ref2, asiento);
				
				
			
			}
		}	
		
	}
	
	
	

}
