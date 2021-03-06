package com.luxsoft.siipap.service.ventas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ca.odell.glazedlists.GlazedLists;

import com.luxsoft.siipap.cxc.model.Abono;
import com.luxsoft.siipap.cxc.model.Aplicacion;
import com.luxsoft.siipap.cxc.model.AplicacionDePago;
import com.luxsoft.siipap.cxc.model.AutorizacionParaCargo;
import com.luxsoft.siipap.cxc.model.CancelacionDeCargo;
import com.luxsoft.siipap.cxc.model.Cargo;
import com.luxsoft.siipap.cxc.model.OrigenDeOperacion;
import com.luxsoft.siipap.cxc.model.PagoConCheque;
import com.luxsoft.siipap.cxc.model.PagoConDeposito;
import com.luxsoft.siipap.cxc.model.PagoConEfectivo;
import com.luxsoft.siipap.cxc.model.PagoConTarjeta;
import com.luxsoft.siipap.cxc.model.PagoDeDiferencias;
import com.luxsoft.siipap.cxc.rules.RevisionDeCargosRules;
import com.luxsoft.siipap.dao.core.FolioDao;
import com.luxsoft.siipap.inventarios.dao.ExistenciaDao;
import com.luxsoft.siipap.inventarios.model.Existencia;
import com.luxsoft.siipap.model.CantidadMonetaria;
import com.luxsoft.siipap.model.Periodo;
import com.luxsoft.siipap.model.core.Folio;
import com.luxsoft.siipap.service.KernellSecurity;
import com.luxsoft.siipap.util.MonedasUtils;
import com.luxsoft.siipap.ventas.dao.VentaDao;
import com.luxsoft.siipap.ventas.model.Cobrador;
import com.luxsoft.siipap.ventas.model.Vendedor;
import com.luxsoft.siipap.ventas.model.Venta;
import com.luxsoft.siipap.ventas.model.VentaDet;
import com.luxsoft.sw3.ventas.Pedido;
import com.luxsoft.sw3.ventas.PedidoDet;

@Service("facturasManager")
@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
public class FacturasManagerImpl  implements FacturasManager{
	
	@Autowired
	private VentaDao ventaDao;
	
	@Autowired
	private FolioDao folioDao;
	
	@Autowired
	private ExistenciaDao existenciaDao;	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	private Logger logger=Logger.getLogger(getClass());
		
	@Transactional(propagation=Propagation.REQUIRED)
	public Venta getFactura(String id) {
		Venta v=ventaDao.get(id);
		hibernateTemplate.initialize(v.getPedido());
		return v;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Venta buscarVentaInicializada(final String ventaId){
		Venta v=getFactura(ventaId);
		Hibernate.initialize(v.getCliente());
		Hibernate.initialize(v.getCliente().getTelefonos());
		Hibernate.initialize(v.getPartidas());
		Hibernate.initialize(v.getVendedor());
		Hibernate.initialize(v.getCobrador());
		if(v.getPedido()!=null){
			Hibernate.initialize(v.getPedido());
			Hibernate.initialize(v.getPedido().getInstruccionDeEntrega());
		}
		Hibernate.initialize(v.getSocio());
		return v;
	}
	
	public List<Aplicacion> buscarAplicaciones(final String ventaId){
		String hql="from Aplicacion a where a.cargo.id=?";
		return hibernateTemplate.find(hql, ventaId);
	}
	
	@Transactional(propagation=Propagation.NEVER,readOnly=true)
	public List<Venta> prepararParaFacturar(Pedido pedido,final Date fecha) {		
		List<Venta> res=new ArrayList<Venta>();
		String tipo="FAC_"+pedido.getOrigen();
		Folio folio=folioDao.buscarNextFolio(pedido.getSucursal(), tipo);
		//long folio=buscarFolioDeFactura(pedido);
		if(pedido.getPartidas().size()<=12){
			Venta fac=generarFactura(pedido,fecha);
			
			
			fac.setDocumento(folio.next());
			fac.setNumeroFiscal(folio.next().intValue());
			for(PedidoDet det:pedido.getPartidas()){
				VentaDet vdet=det.toVentaDet();
				vdet.setDocumento(fac.getDocumento());
				vdet.setDescuentoOriginal(pedido.getDescuentoOrigen());
				//if(!det.getProducto().isPrecioBruto() && !fac.getOrigen().equals(OrigenDeOperacion.CRE))
				if(!det.getProducto().isPrecioBruto() && (fac.getOrigen().equals(OrigenDeOperacion.CAM) || fac.getOrigen().equals(OrigenDeOperacion.MOS) || fac.isPostFechado()))	
					vdet.setDescuentoOriginal(0.0);
				fac.agregarPartida(vdet);
				
			}
			CantidadMonetaria flete=new CantidadMonetaria(pedido.getFlete(),pedido.getMoneda());
			CantidadMonetaria cargos=new CantidadMonetaria(pedido.getComisionTarjetaImporte(),pedido.getMoneda());
			//cargos=cargos.add(flete);
			fac.setCargos(cargos.amount());
			fac.setFlete(flete.amount());
			fac.setImporte(pedido.getSubTotal());
			fac.setImpuesto(pedido.getImpuesto());
			fac.setTotal(pedido.getTotal());
			//fac.setCobrador(Cliente.getCobrador());
			res.add(fac);
			
		}else{
			List<PedidoDet> partidas=new ArrayList<PedidoDet>(pedido.getPartidas());
			logger.info("Procesando pedido con :"+partidas.size()+ "partidas");
			List<PedidoDet> target=new ArrayList<PedidoDet>();
			Collections.sort(partidas,GlazedLists.beanPropertyComparator(PedidoDet.class, "log.creado"));
			for(int index=0;index<partidas.size();index++){
				target.add(partidas.get(index));
				if((index+1)%12==0){
					Venta fac=prepararFactura(pedido, target, folio.next(),fecha);
					target.clear();
					res.add(fac);
				}
			}
			if(!target.isEmpty()){
				Venta fac=prepararFactura(pedido, target, folio.next(),fecha);
				res.add(fac);
			}
			//Apliamos los cargos y demas solo a la primera factura
			Venta fac=res.get(0);
			CantidadMonetaria flete=new CantidadMonetaria(pedido.getFlete(),pedido.getMoneda());
			CantidadMonetaria cargos=new CantidadMonetaria(pedido.getComisionTarjetaImporte(),pedido.getMoneda());			
			//cargos=cargos.add(flete); 			
			fac.setCargos(cargos.amount());
			fac.setFlete(flete.amount());
			CantidadMonetaria importe=fac.getImporteCM();
			importe=importe.add(flete).add(cargos);
			fac.setImporte(importe.amount());
			fac.setImpuesto(MonedasUtils.calcularImpuesto(importe).amount());
			fac.setTotal(MonedasUtils.calcularTotal(importe).amount());
		}
		
		return res;
	}
	
	private Venta prepararFactura(final Pedido pedido,List<PedidoDet> partidas,long folio,final Date fecha){
		Venta fac=generarFactura(pedido,fecha);		
		fac.setDocumento(folio);
		fac.setNumeroFiscal((int)folio);
		CantidadMonetaria importe=new CantidadMonetaria(0,pedido.getMoneda());
		for(PedidoDet det:partidas){
			VentaDet vdet=det.toVentaDet();
			vdet.setDescuentoOriginal(pedido.getDescuentoOrigen());
			
			if(!det.getProducto().isPrecioBruto() && !fac.getOrigen().equals(OrigenDeOperacion.CRE))
				vdet.setDescuentoOriginal(0.0);
				
			vdet.setDocumento(folio);
			fac.agregarPartida(vdet);
			CantidadMonetaria imp=new CantidadMonetaria(det.getSubTotal(),pedido.getMoneda());
			importe=importe.add(imp);
		}
		CantidadMonetaria impuesto=MonedasUtils.calcularImpuesto(importe);
		CantidadMonetaria total=importe.add(impuesto);
		fac.setImporte(importe.amount());
		fac.setImpuesto(impuesto.amount());
		fac.setTotal(total.amount());
		return fac;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public List<Venta> facturar(List<Venta> facturas) {
		List<Venta> res=new ArrayList<Venta>();
		for(Venta fac:facturas){			
			fac=persistir(fac);
			res.add(fac);
		}
		return res;
	}	
		
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	private Venta generarFactura(final Pedido pedido,final Date fecha){
		Venta f=new Venta(pedido);
		f.setFecha(fecha);
		if(pedido.isDeCredito())
			RevisionDeCargosRules.instance().actualizar(f, fecha);
		return f;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public List<Venta> facturarYAplicar(final List<Abono> pagos,final List<Venta> facturas){
		//Assert.notEmpty(pagos);
		List<Venta> res=new ArrayList<Venta>();
		if(pagos==null || pagos.isEmpty()){
			for(Venta fac:facturas){
				fac=persistir(fac);
				res.add(fac);
				
			}
			return res;
		}
		Iterator<Abono> iterator=pagos.iterator();		
		Abono pago=iterator.next();
		CantidadMonetaria disponible=pago.getDisponibleEnLinea();
		
		Date fecha=null; //Fecha para usar en abon.diferenciaFecha en caso de ser necesario (abon.dispnible<=10)
		
		for(Venta fac:facturas){
			if(fecha==null){
				fecha=fac.getFecha();
			}
			fac=persistir(fac);
			res.add(fac);
			CantidadMonetaria porPagar=fac.getTotalCM();
			
			while(porPagar.amount().doubleValue()>0){
				
				if(disponible.amount().doubleValue()<=0){
					if(iterator.hasNext()){
						pago=iterator.next();
						disponible=pago.getDisponibleEnLinea();
					}
					else
						break;
				}
				
				pago.setNombre(fac.getNombre());
				CantidadMonetaria porAplicar=new CantidadMonetaria(0,porPagar.currency());
				while(disponible.getAmount().doubleValue()>0){
					if(porPagar.compareTo(disponible)<=0){
						porAplicar=porAplicar.add(porPagar);
						disponible=disponible.subtract(porAplicar);
						porPagar=new CantidadMonetaria(0,fac.getMoneda());
						break; //Ya no se requiere mas de este pago
					}else{
						porAplicar=porAplicar.add(disponible);
						disponible=new CantidadMonetaria(0,fac.getMoneda());
						porPagar=porPagar.subtract(porAplicar);						
					}
				}
				AplicacionDePago a=new AplicacionDePago();
				a.setCargo(fac);				
				a.setFecha(fac.getFecha());
				a.setImporte(porAplicar.amount());				
				pago.agregarAplicacion(a);
				a.actualizarDetalle();
			}			
		}
		
		
		
		for(Abono abono:pagos){			
			if( (abono instanceof PagoConTarjeta) 
					||( pago instanceof PagoConCheque )
					||( pago instanceof PagoConEfectivo)
					){
				
				abono.setLiberado(pago.getFecha());
			}
			if(abono.getDisponibleEnLinea().amount().doubleValue()<=10.00d){
				abono.setDiferencia(abono.getDisponibleEnLinea().amount());
				abono.setDirefenciaFecha(fecha!=null?fecha:new Date());
			}
			abono.setImportado(null);
			hibernateTemplate.merge(abono);
		}
		
		return res;
	}
		
	/**
	 * Genera un abono autmaic para facturas con saldo <=1 peso
	 * 
	 * @param facturas
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void generarAbonoAutmatico(final List<Venta> facturas){
		
		for(Venta fac:facturas){
			Venta target=getVentaDao().get(fac.getId());
			BigDecimal saldo=target.getSaldoCalculado();
			if(saldo.doubleValue()<=1.00d){
				if(saldo.doubleValue()>0.00d){
					System.out.println("Generar abono automatico para venta: "+target.getDocumento());
					generarPagoPorDiferencia(target, false);
				}
			}
		}
	}
	
	@Transactional(propagation=Propagation.MANDATORY)
	public void generarPagoPorDiferencia(final Cargo cargo,final boolean cambiaria){
		if(!cargo.getTipoSiipap().equals("X"))
			Assert.isTrue(cargo.getSaldoCalculado().doubleValue()<100,"No se permite pago de diferencias mayores a 100");
		Assert.isTrue(cargo.getSaldoCalculado().doubleValue()>0,"No se permite pago de facturas con saldos negativos");
		PagoDeDiferencias pago=new PagoDeDiferencias();
		pago.setCambiaria(cambiaria);
		pago.setCliente(cargo.getCliente());
		pago.setComentario("OTROS PRODUCTOS");
		pago.setSucursal(cargo.getSucursal());
		AplicacionDePago aplicacion=new AplicacionDePago();
		aplicacion.setCargo(cargo);
		aplicacion.setImporte(cargo.getSaldoCalculado());
		aplicacion.setComentario("OTROS PRODUCTOS");
		aplicacion.actualizarDetalle();
		aplicacion.getDetalle().setFormaDePago(pago.getInfo());
		pago.agregarAplicacion(aplicacion);
		
		pago.setImporte(MonedasUtils.calcularImporteDelTotal(aplicacion.getImporte()));
		pago.actualizarImpuesto();
		pago.setTotal(aplicacion.getImporte());
		pago.setImportado(null);
		pago=(PagoDeDiferencias)hibernateTemplate.merge(pago);
		
	}
		
	@Transactional(propagation=Propagation.REQUIRED)
	public Venta cancelarFactura(String id,final Date fecha) {
		Venta cargo=getFactura(id);
		
		//Paso 1 Generar una autorizacion
		AutorizacionParaCargo aut=new AutorizacionParaCargo();
		aut.setAutorizo(KernellSecurity.instance().getCurrentUserName());
		aut.setComentario("CANCELACION DE FACTURA");
		aut.setFechaAutorizacion(fecha);
		aut.setIpAdress(KernellSecurity.getIPAdress());
		aut.setMacAdress(KernellSecurity.getMacAdress());
		
		//Paso 2 Generamos la cancelacion
		final CancelacionDeCargo cancelacion=new CancelacionDeCargo();
		
		
		cancelacion.setComentario("CANCELACION");
		cancelacion.setDocumento(cargo.getDocumento());
		cancelacion.setImporte(cargo.getTotal());
		cancelacion.setMoneda(cargo.getMoneda());
		cancelacion.setAutorizacion(aut);
		cancelacion.setFecha(fecha);
		cancelacion.getLog().setCreado(cancelacion.getFecha());
		cancelacion.getLog().setCreateUser(KernellSecurity.instance().getCurrentUserName());
		
		cargo.setImporte(BigDecimal.ZERO);
		cargo.setImpuesto(BigDecimal.ZERO);
		cargo.setTotal(BigDecimal.ZERO);
		cargo.setFlete(BigDecimal.ZERO);
		cargo.setCargos(BigDecimal.ZERO);
		//cargo.setAutorizacionSinExistencia(null);
		actualizarInventarioPorCancelacion(cargo);
		cargo.getPartidas().clear();
		
		cargo.setComentario2("FACTURA CANCELADO");
		
		cancelacion.setCargo(cargo);
		cargo.setCancelacion(cancelacion);
		
		this.ventaDao.save(cargo);
		hibernateTemplate.save(cancelacion);
		//cancelarPagos(cargo);
		
		for(Aplicacion a:cargo.getAplicaciones()){
			if(a.getAbono().getAplicaciones().size()==1){
				if(!(a.getAbono() instanceof PagoConDeposito))
					hibernateTemplate.delete(a.getAbono());
			}else{
				hibernateTemplate.delete(a);
			}
		}
		
		return getFactura(cargo.getId());
	}
		
	@Transactional(propagation=Propagation.REQUIRED)
	public Venta cancelarFactura(CancelacionDeCargo cancelacion,final String id,final Date fecha){
		
		Venta cargo=getFactura(id);		
		cargo.setImporte(BigDecimal.ZERO);
		cargo.setImpuesto(BigDecimal.ZERO);
		cargo.setTotal(BigDecimal.ZERO);
		cargo.setFlete(BigDecimal.ZERO);
		cargo.setCargos(BigDecimal.ZERO);
		actualizarInventarioPorCancelacion(cargo);
		cargo.getPartidas().clear();		
		cargo.setComentario2("FACTURA CANCELADO");
		
		cancelacion.setCargo(cargo);
		cargo.setCancelacion(cancelacion);
		
		this.ventaDao.save(cargo);
		hibernateTemplate.save(cancelacion);
		for(Aplicacion a:cargo.getAplicaciones()){
			hibernateTemplate.delete(a);
		}
		return getFactura(cargo.getId());
	}

	@Transactional(propagation=Propagation.MANDATORY)
	private Venta persistir(Venta factura){
		int renglon=1;
		
		String tipo="FAC_"+factura.getOrigen().name();
		Folio folio=folioDao.buscarNextFolio(factura.getSucursal(), tipo);
		Long docto=folio.getFolio();
		folio=folioDao.save(folio);
		factura.setDocumento(docto);
		if(!factura.getOrigen().equals(OrigenDeOperacion.CRE)){
			factura.setNumeroFiscal(docto.intValue());
		}
		
		Collections.sort(factura.getPartidas(),GlazedLists.beanPropertyComparator(VentaDet.class, "ordenp"));
		for(VentaDet det:factura.getPartidas()){
			det.setRenglon(renglon);
			det.setDocumento(factura.getDocumento());
			det.setFecha(factura.getFecha());
			renglon++;
		}
		if(factura.getVendedor()==null){
			Vendedor vendedor=(Vendedor)hibernateTemplate.get(Vendedor.class, new Long(1L));
			factura.setVendedor(vendedor);
		}
		if(factura.getCobrador()==null){
			Cobrador cobrador=(Cobrador)hibernateTemplate.get(Cobrador.class, new Long(1l));
			factura.setCobrador(cobrador);
		}
		
		Venta res=(Venta)hibernateTemplate.merge(factura);
		actualizarInventario(res);
		
		
		return res;
	}
	
	@Transactional(propagation=Propagation.MANDATORY)
	private void actualizarInventario(Venta factura){
		final Date hoy=new Date();
		final Long sucursal=factura.getSucursal().getId();
		final int year=Periodo.obtenerYear(hoy);
		final int mes=Periodo.obtenerMes(hoy)+1;
		for(VentaDet det:factura.getPartidas()){
			if(!det.getProducto().isInventariable())
				continue;
			Existencia exis=existenciaDao.buscar(det.getClave(), sucursal, year, mes);
			
			if(exis==null){
				exis=existenciaDao.generar(det.getProducto(), det.getFecha(),det.getSucursal().getId());
			}
			
			if(factura.getAutorizacionSinExistencia()==null){				
				exis.setCantidad(exis.getCantidad()+det.getCantidad());
				existenciaDao.save(exis);
			}else{				
				exis.setCantidad(exis.getCantidad()+det.getCantidad());
				exis=existenciaDao.save(exis);
				logger.info("Inventario actualizado en negativo: "+exis);
			}
		}
	}
	
	@Transactional(propagation=Propagation.MANDATORY)
	private void actualizarInventarioPorCancelacion(Venta factura){
		final Date hoy=new Date();
		final Long sucursal=factura.getSucursal().getId();
		final int year=Periodo.obtenerYear(hoy);
		final int mes=Periodo.obtenerMes(hoy)+1;
		for(VentaDet det:factura.getPartidas()){
			if(!det.getProducto().isInventariable()){
				continue;
			}
			Existencia exis=existenciaDao.buscar(det.getClave(), sucursal, year, mes);
			if(exis==null){
				exis=existenciaDao.generar(det.getProducto(), det.getFecha(), det.getSucursal().getId());
			}			
			exis.setCantidad(exis.getCantidad()+Math.abs(det.getCantidad()));
			existenciaDao.save(exis);
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public VentaDao getVentaDao() {
		return ventaDao;
	}

	public void setVentaDao(VentaDao ventaDao) {
		this.ventaDao = ventaDao;
	}

	public ExistenciaDao getExistenciaDao() {
		return existenciaDao;
	}

	public FolioDao getFolioDao() {
		return folioDao;
	}

	public void setFolioDao(FolioDao folioDao) {
		this.folioDao = folioDao;
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public void setExistenciaDao(ExistenciaDao existenciaDao) {
		this.existenciaDao = existenciaDao;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
