  
  Reporte diario del env�o automatico de comprobantes fiscales digitales (CFD) as� como archivos PDF
  
  Resumen: 
  
  Fecha de generaci�n: 		${fecha?date}
  N�mero total de comprobantes generados: ${total}
  N�mero de comprobantes enviados exitosamente al  los clientes: ${totalExitosos}
  N�mero de comprobantes sin enviar: ${totalFallidos}
  	
  Detalle: 
  Los siguientes comprobantes no  fueron enviados  por no tener registrado un correo electr�nico v�lido
  <#list fallidos as cfd>
			${cfd.tipo} ${cfd.serie}-${cfd.folio}  Receptor: ${cfd.receptor} ${cfd.log.creado?date} Total: ${cfd.totalAsString}
  </#list>  

  
Este correo es enviado de forma autom�tico por el sistema SiipapWin Ex, para cualquier aclaraci�n favor de contactar 
al departamento de sistemas
  