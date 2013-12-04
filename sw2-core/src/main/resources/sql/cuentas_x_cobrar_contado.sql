SELECT V.CARGO_ID as ID,v.tipo,V.DOCTO as documento,V.FECHA,V.MONEDA,V.TC
,CASE WHEN ROUND(TO_DAYS(CURRENT_DATE)-TO_DAYS(V.FECHA),0) <0 THEN 0 ELSE ROUND(TO_DAYS(CURRENT_DATE)-TO_DAYS(V.VTO),0) END AS ATRASO
,S.NOMBRE AS sucursal,V.CLAVE ,V.NOMBRE,V.TOTAL
,ifnull((select sum(X.importe) FROM sx_cxc_aplicaciones x where x.CARGO_ID=v.CARGO_ID AND X.TIPO='NOTA' AND ABN_DESCRIPCION LIKE 'DEV%'),0) as DEVOLUCIONES
,ifnull((select sum(X.importe) FROM sx_cxc_aplicaciones x where x.CARGO_ID=v.CARGO_ID AND X.TIPO='NOTA' AND ABN_DESCRIPCION LIKE 'BON%'),0) as BONIFICACIONES
,ifnull((select sum(X.importe) FROM sx_cxc_aplicaciones x where x.CARGO_ID=v.CARGO_ID AND X.TIPO='PAGO'),0) as PAGOS
,V.TOTAL-IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.CARGO_ID=V.CARGO_ID),0) AS SALDO 
FROM sx_ventas V 
JOIN sw_sucursales S ON(V.SUCURSAL_ID=S.SUCURSAL_ID)
WHERE V.FECHA>'2010/06/30' AND V.CLAVE LIKE '%' AND V.ORIGEN='CAM' AND
V.TOTAL-IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.CARGO_ID=V.CARGO_ID),0)<>0
AND V.CARGO_ID NOT IN(SELECT X.CARGO_ID FROM sx_juridico X)