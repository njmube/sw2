package com.luxsoft.siipap.pos.ui.forms;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.model.FormaDePago;
import com.luxsoft.siipap.model.Direccion;
import com.luxsoft.siipap.model.core.Cliente;
import com.luxsoft.sw3.ventas.Pedido;
import com.luxsoft.sw3.ventas.PedidoDet;
import com.luxsoft.sw3.ventas.Pedido.FormaDeEntrega;

/**
 * Auxiliar en la validacion de pedidos especiales
 * 
 * @author Ruben Cancino Ramos
 *
 */
public class PedidoEspecialFormValidator {
	
	private Pedido pedido;

	public void validate(final Pedido pedido,PropertyValidationSupport support) {
		
		setPedido(pedido);		
		Cliente c=getPedido().getCliente();
		if(c!=null){
			if(c.isSuspendido()){
				support.getResult().addError("Cliente suspendido PARA TODO TIPO DE VENTA");
			}
			if(c.isJuridico()){
				support.getResult().addError( "Cliente en tr�mite jur�dico por lo que no se le puede facturar.\n Pedir autorizaci�n al departamento de cr�dito");
			}
			if(c.getChequesDevueltos().doubleValue()>0){
				support.getResult().addError( "El cliente tiene cheque(s) devueltos por un monto de: "+c.getChequesDevueltos()+" NO SE LE PUEDE FACTURAR.");
			}
		}
		if(pedido.getTotal().doubleValue()<10){
			support.getResult().addError("El monto m�nimo para generar un pedido es de 10.00 pesos");
		}
		
		if(getPedido().isDeCredito())
			validarCondicionesDeCredito(support);
		
		validarFormaDePago(support);
		validarFormaDeEnvio(support);
		if(getPedido().getPartidas().isEmpty())
			support.getResult().addError("El pedido sin partidas no es v�lido");
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
		validarPedidoInicial(support);
	}
	
	private void validarPedidoInicial(PropertyValidationSupport support){
		if(getPedido().isEspecial() && getPedido().getId()==null){
			//validar que solo tenga partidas de tipo especial
			for(PedidoDet det:getPedido().getPartidas()){
				if(!det.isEspecial()){
					support.getResult().addError("Un Pedido nuevo solo puede contener medidas especiales ");
				}
			}
		}
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
								
		}else if(FormaDePago.CHEQUE.equals(getPedido().getFormaDePago())){
			if(c!=null && (!c.isPermitirCheque()))				
				support.getResult().addError("A este cliente no se le permite cheque ");
					
								
		}
	}
	
	private void validarFormaDeEnvio(PropertyValidationSupport support){
		if(getPedido().getEntrega().name().startsWith("ENVIO")){
			if(getPedido().getInstruccionDeEntrega()==null){
				support.getResult().addError("Debe definir la direcci�n de envio");
			}
		}else if(FormaDeEntrega.ENVIO_CARGO.equals(getPedido().getEntrega())){
			if(getPedido().getFlete().doubleValue()<=0)
				support.getResult().addError("El tipo de envio con cargo no aplica para la poblaci�n/estado definidos");
		}
	}
	
	
	
	private void validarCondicionesDeCredito(PropertyValidationSupport support){
		Cliente c=getPedido().getCliente();
		if(c==null)	return;
		
		//Validar q el cliente tenga credito
		if(!c.isDeCredito()){
			support.getResult().addError("El cliente no tiene l�nea de cr�dito");
			return;
		} 
		
		//Valida credito no suspendido
		if(c.getCredito().isSuspendido())
			support.getResult().addError( "Cr�dito suspendido temporalmente ");	
		
		//Valida la linea de credito
		if(c.getCredito().getCreditoDisponible().doubleValue()<getPedido().getTotal().doubleValue()){
			support.getResult().addError( "L�nea de cr�dito SATURADA");
		}
		
		//Valida el atraso maximo del cliente
		if(c.getCredito().getAtrasoMaximo()>15){
			support.getResult().addError( "El cliente tiene un atraso superior a 15 dias (Llamar a Cr�dito)");
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
				support.getResult().addError( "La direcci�n del cliente no puede ser nula");
			}
		}
	}
	
	
	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	

}
