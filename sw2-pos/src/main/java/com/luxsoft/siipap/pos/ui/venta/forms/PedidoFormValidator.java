package com.luxsoft.siipap.pos.ui.venta.forms;

import java.text.MessageFormat;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.model.FormaDePago;
import com.luxsoft.siipap.model.Direccion;
import com.luxsoft.siipap.model.core.Cliente;
import com.luxsoft.siipap.util.MonedasUtils;
import com.luxsoft.sw3.ventas.Pedido;
import com.luxsoft.sw3.ventas.Pedido.ClasificacionVale;
import com.luxsoft.sw3.ventas.PedidoDet;
import com.luxsoft.sw3.ventas.Pedido.FormaDeEntrega;

/**
 * Auxiliar en la validacion de pedidos, util para simplificar el bean PedidoController
 * 
 * @author Ruben Cancino Ramos
 *
 */
public class PedidoFormValidator {
	
	private Pedido pedido;

	public void validate(final Pedido pedido,PropertyValidationSupport support) {
		
		setPedido(pedido);		
		Cliente c=getPedido().getCliente();
		if(c!=null){
			if(c.isSuspendido()){
				support.getResult().addError("Cliente suspendido PARA TODO TIPO DE VENTA");
			}
			if(c.isJuridico()){
				support.getResult().addError( "Cliente en tramite jurdico por lo que no se le puede facturar.\n Pedir autorizacin al departamento de credito");
			}
			if(c.getChequesDevueltos().doubleValue()>0){
				support.getResult().addError( "El cliente tiene cheque(s) devueltos por un monto de: "+c.getChequesDevueltos()+" NO SE LE PUEDE FACTURAR.");
			}
		}
		if(pedido.getTotal().doubleValue()<0){
			//support.getResult().addError("El monto mnimo para generar un pedido es de 10.00 pesos");
			support.getResult().addError("El monto para generar un pedido debe ser mayor a 0.00 pesos");
		}
		
		// Papel: Modificacion al validador para salvar pedidos con anticipo y totales en cero
		
				Set<PedidoDet> partidas=  pedido.getPartidas();
				
				boolean pasa=false; 
				for (PedidoDet p :partidas)
					{
					 if(p.getClave().equals("ANTICIPO"))
						{
						 pasa= true;
						 break;
						}
					 }
				 if (pasa)	
				 	{
					 if(pedido.getTotal().doubleValue()<0){
						 support.getResult().addError("El monto para facturar no debe ser menor a 0");
					 	}
				 }
				 /*else
				 {
					 if(pedido.getTotal().doubleValue()<10){
						 support.getResult().addError("El monto mnimo para generar un pedido es de 10.00 pesos");
					 		}
				 }*/
		
		
		if(getPedido().isDeCredito())
			validarCondicionesDeCredito(support);
		
		validarFormaDePago(support);
		validarFormaDeEnvio(support);
		if(getPedido().getPartidas().isEmpty())
			support.getResult().addError("El pedido sin partidas no es vlido");
		if(StringUtils.isBlank(getPedido().getComprador())){
			support.getResult().addError("Registre el nombre de la persona que levanta el pedido ");
		}
		validarDireccion(support);
		
		if(c!=null){
			String MSG_VENTAS=c.getComentarios().get("VTA_MSG_RR");
			if(StringUtils.isNotBlank(MSG_VENTAS)){
				support.getResult().addWarning(MSG_VENTAS);
			}
		}
		
		//Validar si es anticipo
		if(getPedido().isAnticipo()){
			if(getPedido().getPartidas().size()!=1){
				support.getResult().addError("Un anticipo solo puede tener una partida de tipo ANTICIPO ");
				return;
			}else{
				PedidoDet det=getPedido().getPartidas().iterator().next();
				if(!det.getProducto().getClave().equals("ANTICIPO")){
					support.getResult().addError("Un anticipo solo puede tener una partida de tipo ANTICIPO ");
				}
				if(det.getProducto().isInventariable()){
					support.getResult().addError("Un anticipo no puede ser inventariable ");
				}
			}
			
		}
		//Validar Tipo de cambio
		if(getPedido().getMoneda().equals(MonedasUtils.DOLARES)){
			if(getPedido().getTc()<=1){
				support.getResult().addError("No existe en el sistema el tipo de cambio para el dia ");
			}
		}
		validarIsConVale(support);
		validarSucursalVale(support);
		validarIsCotizacion(support);
		validarPartidasVale(support);
		/*
		for(PedidoDet det:getPedido().getPartidas()){
			double existenciaTotal=0; //det
			double existenciaSucursal=0;//det
			if( (det.getProducto()!=null) && det.getProducto().getLinea().getId().longValue()!= 106L){
				if(det.getCantidad()>existenciaTotal){
					if(det.getCantidad()>existenciaSucursal){
						support.getResult().addError("No hay existencia suficiente para facturar: "+det.getCantidad() 
								+" Existencia Total"+ existenciaTotal +" Existencia sucursal: "+existenciaSucursal);
					}
				}
			}
		}*/
	}
	
	
	
	private void validarFormaDePago(PropertyValidationSupport support){
		Cliente c=getPedido().getCliente();
		if(FormaDePago.CHEQUE_POSTFECHADO.equals(getPedido().getFormaDePago())){
			if(c!=null && c.isDeCredito())				
				if(!c.getCredito().isChequePostfechado()){
					//getValidationModel().getResult().addError("A este cliente no se le permite cheque post- fechado");
					support.getResult().addError("A este cliente no se le permite cheque post - fechado");
				}
			if(!getPedido().isDeCredito()){
				support.getResult().addError("No se permite Cheque post fechado en contado");
			}
								
		}else if(FormaDePago.CHECKPLUS.equals(getPedido().getFormaDePago())){
			if(c!=null && c.isDeCredito())				
				if(!c.getCredito().isCheckplus()){
					support.getResult().addError("A este cliente no se le permite forma de pago CheckPlus");
				}
			if(!getPedido().isDeCredito()){
				support.getResult().addError("No se permite CheckPlus en contado");
			}
		}else if(FormaDePago.CHEQUE.equals(getPedido().getFormaDePago())){
			if(c!=null && (!c.isPermitirCheque()))				
				support.getResult().addError("A este cliente no se le permite cheque ");
					
								
		}
	}
	
	private void validarFormaDeEnvio(PropertyValidationSupport support){
		if(getPedido().getEntrega().name().startsWith("ENVIO")){
			if(getPedido().getInstruccionDeEntrega()==null){
				support.getResult().addError("Debe definir la direccion de envio");
			}
		}else if(FormaDeEntrega.ENVIO_CARGO.equals(getPedido().getEntrega())){
			if(getPedido().getFlete().doubleValue()<=0)
				support.getResult().addError("El tipo de envio con cargo no aplica para la poblacion/estado definidos");
		}
	}
	
	
	
	private void validarCondicionesDeCredito(PropertyValidationSupport support){
		Cliente c=getPedido().getCliente();
		if(c==null)	return;
		
		//Validar q el cliente tenga credito
		if(!c.isDeCredito()){
			support.getResult().addError("El cliente no tiene lnea de credito");
			return;
		} 
		
		//Valida credito no suspendido
		if(c.getCredito().isSuspendido())
			//support.getResult().addError( "Crdito suspendido temporalmente ");	
			support.getResult().addError( "ENLAZAR LA LLAMADA DEL CLIENTE A CREDITO (Venta a Credito Suspendida)");	
		
		//Valida la linea de credito
		if(c.getCredito().getCreditoDisponible().doubleValue()<getPedido().getTotal().doubleValue()){
			//support.getResult().addError( "Lnea de crdito SATURADA");
			support.getResult().addError( "ENLAZAR LA LLAMADA DEL CLIENTE A CREDITO (Linea de Credito Saturada)");
			
		}
		
		//Valida el atraso maximo del cliente
		int plazo=c.getCredito().getPlazo();
		int atrasoMax=20;
		switch (plazo) {
		case 30:
			atrasoMax=15;
			break;
		case 45:
			atrasoMax=7;
			break;
		case 60:
			atrasoMax=7;
			break;
		case 75:
			atrasoMax=1;
			break;
		case 90:
			atrasoMax=1;
			break;
		default:
			break;
		}
		 if (!c.getCredito().isNoAtraso())
		 {
			 if(c.getCredito().getAtrasoMaximo()>atrasoMax){
					String pattern="El cliente tiene un atraso superior a {0} dias --";
					String pattern2=pattern+"\nENLAZAR LA LLAMADA DEL CLIENTE A CREDITO";
					support.getResult().addError(MessageFormat.format(pattern2, atrasoMax));
				}
		 }
		
		/*
		 * // Modificacion por ordenes de Direccion General (Lic. Jose Sanchez) 14/08/2013
		if(c.getCredito().getAtrasoMaximo()>15){
			support.getResult().addError( "El cliente tiene un atraso superior a 15 dias (Llamar a Crdito)");
		}*/
		
	}
	
	
	
	private void validarIsCotizacion(PropertyValidationSupport support){
		if(!getPedido().getPartidas().isEmpty()){
			int valid=0;
			
			for(PedidoDet det : getPedido().getPartidas()){
				if(det.isCotizable()){
					valid=valid+1;
				}
			}
			if(valid>0 && getPedido().getComentario2()==null){
				support.getResult().addError( "Se Requiere autorizacion para gravar sin Existencia ");
			}
			
		}
		
	}
	
	
	private void validarIsConVale(PropertyValidationSupport support){
		boolean conVale=false;
		for(PedidoDet det : getPedido().getPartidas()){
			System.out.println("Validando si el pedido tiene vale");
			if(det.isConVale() && getPedido().getClasificacionVale().equals(ClasificacionVale.SIN_VALE)){
				System.out.println("El pedido si tiene vale");
				conVale=true;
				break;
			}
		}
		if(conVale){
			support.getResult().addError("El pedido requiere Vale favor de seleccionar una clasificacion para Vale");
		}
	
			
	}
	
	private void validarPartidasVale(PropertyValidationSupport support){
		int conVale=0;
			for(PedidoDet det : getPedido().getPartidas()){
				if(det.isConVale()){
					conVale=conVale+1;
				}
			}
			if(conVale==0 && !getPedido().getClasificacionVale().equals(ClasificacionVale.SIN_VALE) ){
			support.getResult().addError("El pedido no tiene partidas para vale");
		}
		
	}
	
	
	private void validarDireccion(PropertyValidationSupport support){
		if(getPedido().getEntrega().equals(FormaDeEntrega.LOCAL))
			return;
		if(getPedido().getCliente()!=null){
			Cliente c=getPedido().getCliente();
			if("1".equals(c.getClave()))
				return;
			if(c.getDireccionFiscal()!=null ){
				Direccion d=c.getDireccionFiscal();
				if(	StringUtils.isBlank(d.getCalle())
					||StringUtils.isBlank(d.getCp())
					||StringUtils.isBlank(d.getColonia()))
				{
					
					support.getResult().addError( "Direccion incorrecta");
				}
			}else{
				support.getResult().addError( "La direccion del cliente no puede ser nula");
			}
		}
	}
	
	private void validarSucursalVale(PropertyValidationSupport support){
		if(!getPedido().getClasificacionVale().equals(ClasificacionVale.SIN_VALE) && getPedido().getSucursalVale()==null){
			support.getResult().addError( "Debe seleccionar la sucursal a la que solicita el vale");
		}
	}
	
	
	
	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	

}
