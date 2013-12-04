<html>
<head>
	<style type="text/css">
		h4 {color: red}
		h3 {color: black}
	</style>

  <title>Reporte de correos CFD</title>
  <h2>Reporte diario del env�o automatico de comprobantes fiscales digitales (CFD) as� como archivos PDF</h2>
</head>
<body>
  <h3>Resumen: 
  	<br>Fecha de generaci�n: 		${fecha?date}
  	<br>N�mero total de comprobantes generados: ${total?string("0")}
  	<br>N�mero de comprobantes enviados exitosamente al  los clientes: ${totalExitosos}
  	<br>N�mero de comprobantes sin enviar: ${totalFallidos}
  </h3>
  <p>Los siguientes comprobantes no  fueron enviados  por no tener registrado un correo electr�nico v�lido
  
  <table align="center"
		valign="middle"
		  width="800" border="1" cellspacing="1" cellpadding="1">
  
	<tr bgcolor="#999999">
	<th>Tipo</th>
	<th>Serie</th>
	<th>Folio</th>
	<th>Receptor</th>
	<th>Fecha</th>
	<th>Total</th>
	
	<#list fallidos as cfd>
		<tr>
			<td align="left">${cfd.tipo}</td>
			<td align="center">${cfd.serie}</td>
			<td align="center">${cfd.folio}</td>
			<td align="center">${cfd.receptor}</td>
			<td align="center">${cfd.log.creado?date}</td>
			<td align="right">${cfd.totalAsString}</td>
			
		</tr>
	</#list>  
  </table>
  
  
</body>
</html>  