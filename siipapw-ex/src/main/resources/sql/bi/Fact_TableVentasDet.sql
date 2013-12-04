drop table VENTASDET_FAC_2013

create table VENTASDET_FAC_2013 as
SELECT 'VTA' AS TIPO
,A.CARGO_ID AS ORIGEN_ID,D.INVENTARIO_ID,A.CLIENTE_ID,(CASE WHEN A.CLAVE=1 THEN 'MOSTRADOR' ELSE A.NOMBRE END) AS CLIENTE,A.DOCTO,A.ORIGEN
,(select s.nombre from sw_sucursales s where d.SUCURSAL_ID=s.SUCURSAL_ID) as SUC,A.FECHA
,L.NOMBRE AS LINEA,M.NOMBRE as MARCA,C.NOMBRE as CLASE,P.PRODUCTO_ID,D.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS as KXMIL,P.CALIBRE,P.CARAS,P.DELINEA
,D.CANTIDAD*-1/D.FACTORU as CANTIDAD,(D.CANTIDAD*-1/D.FACTORU)*P.KILOS as KILOS
,(D.IMPORTE_NETO) AS IMP_NETO,(-D.CANTIDAD/D.FACTORU*D.COSTOP) AS COSTO,0.0 as DESCTO_COSTO, 0.0 as REBATE,0.0 as COSTO_NETO
FROM  SX_VENTASDET D  USE INDEX (INDX_VDET2)  JOIN SX_VENTAS A ON(A.CARGO_ID=D.VENTA_ID) JOIN SX_PRODUCTOS P ON (P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON (L.LINEA_ID=P.LINEA_ID) JOIN sx_marcas M ON (M.MARCA_ID=P.MARCA_ID) JOIN sx_clases C ON (C.CLASE_ID=P.CLASE_ID) 
WHERE P.CLAVE<>'ANTICIPO'   AND D.FECHA BETWEEN '2013-01-01' AND '2013-03-31'

/*
insert into VENTASDET_FAC_2013
SELECT 'VTA' AS TIPO
,A.CARGO_ID AS ORIGEN_ID,D.INVENTARIO_ID,A.CLIENTE_ID,(CASE WHEN A.CLAVE=1 THEN 'MOSTRADOR' ELSE A.NOMBRE END) AS CLIENTE,A.DOCTO,A.ORIGEN
,(select s.nombre from sw_sucursales s where d.SUCURSAL_ID=s.SUCURSAL_ID) as SUC,A.FECHA
,L.NOMBRE AS LINEA,M.NOMBRE as MARCA,C.NOMBRE as CLASE,P.PRODUCTO_ID,D.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS as KXMIL,P.CALIBRE,P.CARAS,P.DELINEA
,D.CANTIDAD*-1/D.FACTORU as CANTIDAD,(D.CANTIDAD*-1/D.FACTORU)*P.KILOS as KILOS
,(D.IMPORTE_NETO) AS IMP_NETO,(-D.CANTIDAD/D.FACTORU*D.COSTOP) AS COSTO
FROM  SX_VENTASDET D  USE INDEX (INDX_VDET2)  JOIN SX_VENTAS A ON(A.CARGO_ID=D.VENTA_ID) JOIN SX_PRODUCTOS P ON (P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON (L.LINEA_ID=P.LINEA_ID) JOIN sx_marcas M ON (M.MARCA_ID=P.MARCA_ID) JOIN sx_clases C ON (C.CLASE_ID=P.CLASE_ID) 
WHERE P.CLAVE<>'ANTICIPO'   AND D.FECHA BETWEEN '2013-04-01' AND '2013-04-30' 
*/
