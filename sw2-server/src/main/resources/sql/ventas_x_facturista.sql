SELECT A.FECHA,A.SUCURSAL_ID,A.CREADO_USR,A.FACTURISTA
,SUM(CASE WHEN A.TIPO='PED' THEN REG ELSE 0 END) AS PED
,SUM(CASE WHEN A.TIPO='MOS' THEN REG ELSE 0 END) AS MOS
,SUM(CASE WHEN A.TIPO='CAM' THEN REG ELSE 0 END) AS CAM
,SUM(CASE WHEN A.TIPO='CRE' THEN REG ELSE 0 END) AS CRE
,SUM(CASE WHEN A.TIPO='CNC' THEN REG ELSE 0 END) AS CANC
,SUM(CASE WHEN A.TIPO IN('MOS','CAM','CRE','CNC') THEN REG ELSE 0 END) AS FACS
,SUM(A.IMPORTE) AS IMPORTE
,SUM(CASE WHEN A.TIPO='PRT' THEN REG ELSE 0 END) AS PART
FROM (
SELECT 'PED' AS TIPO,P.fecha,P.SUCURSAL_ID,P.CREADO_USR,P.CREADO_USR AS FACTURISTA,COUNT(*) AS REG,0 AS IMPORTE
FROM sx_pedidos P WHERE P.FECHA BETWEEN @FECHA_INI AND @FECHA_FIN AND P.SUCURSAL_ID=@SUCURSAL_ID GROUP BY P.fecha,P.SUCURSAL_ID,P.CREADO_USR
UNION
SELECT 'MOS' AS TIPO,V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID,V.MODIFICADO_USERID AS FACTURISTA,COUNT(*) AS REG,SUM(V.IMPORTE) AS IMPORTE
FROM sx_VENTAS V WHERE V.TIPO='FAC' AND V.FECHA BETWEEN @FECHA_INI AND @FECHA_FIN AND V.ORIGEN='MOS' AND V.SUCURSAL_ID=@SUCURSAL_ID GROUP BY V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID
UNION
SELECT 'CAM' AS TIPO,V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID,V.MODIFICADO_USERID AS FACTURISTA,COUNT(*) AS REG,SUM(V.IMPORTE) AS IMPORTE
FROM sx_VENTAS V WHERE V.TIPO='FAC' AND V.FECHA BETWEEN @FECHA_INI AND @FECHA_FIN AND V.ORIGEN='CAM' AND V.SUCURSAL_ID=@SUCURSAL_ID GROUP BY V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID
UNION
SELECT 'CRE' AS TIPO,V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID,V.MODIFICADO_USERID AS FACTURISTA,COUNT(*) AS REG,SUM(V.IMPORTE) AS IMPORTE
FROM sx_VENTAS V WHERE V.TIPO='FAC' AND V.FECHA BETWEEN @FECHA_INI AND @FECHA_FIN AND V.ORIGEN='CRE' AND V.SUCURSAL_ID=@SUCURSAL_ID GROUP BY V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID
UNION
SELECT 'PRT' AS TIPO,V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID,V.MODIFICADO_USERID AS FACTURISTA,COUNT(*) AS REG,0 AS IMPORTE
FROM sx_VENTASDET D JOIN SX_VENTAS V ON(V.CARGO_ID=D.VENTA_ID) WHERE V.TIPO='FAC' AND V.FECHA BETWEEN @FECHA_INI AND @FECHA_FIN AND V.SUCURSAL_ID=@SUCURSAL_ID GROUP BY V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID
UNION
SELECT 'CNC' AS TIPO,V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID,V.MODIFICADO_USERID AS FACTURISTA,COUNT(*)*-1 AS REG,0 AS IMPORTE
FROM sx_cxc_cargos_cancelados C JOIN SX_VENTAS V ON(V.CARGO_ID=C.CARGO_ID) WHERE V.FECHA BETWEEN @FECHA_INI AND @FECHA_FIN AND V.SUCURSAL_ID=@SUCURSAL_ID GROUP BY V.fecha,V.SUCURSAL_ID,V.MODIFICADO_USERID
) A
GROUP BY A.FECHA,A.SUCURSAL_ID,A.CREADO_USR,A.FACTURISTA