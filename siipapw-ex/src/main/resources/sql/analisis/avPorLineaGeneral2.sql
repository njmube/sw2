SELECT 
YEAR,MES,LINEA
,SUM(CASE WHEN SERIE='E' THEN ((-CANTIDAD-DEVOLUCIONES)*KILOS)/1000 ELSE 0 END) AS toneladasCre
,SUM(CASE WHEN SERIE<>'E' THEN ((-CANTIDAD-DEVOLUCIONES)*KILOS)/1000 ELSE 0 END) AS toneladasCon
,SUM( ((-CANTIDAD-DEVOLUCIONES)*KILOS)/1000) AS TON_TOT
,SUM(CASE WHEN SERIE='E' THEN ((-CANTIDAD-DEVOLUCIONES)*PRECIOFACTURADO)-(((-CANTIDAD-DEVOLUCIONES)*PRECIOLISTA)*DESCUENTO/100) ELSE 0 END) AS ventaNetaCre
,SUM(CASE WHEN SERIE<>'E' THEN ((-CANTIDAD-DEVOLUCIONES)*PRECIOFACTURADO)-(((-CANTIDAD-DEVOLUCIONES)*PRECIOLISTA)*DESCUENTO/100) ELSE 0 END) AS ventaNetaCon
,SUM((-CANTIDAD-DEVOLUCIONES)*PRECIOFACTURADO)-SUM(((-CANTIDAD-DEVOLUCIONES)*PRECIOLISTA)*DESCUENTO/100) AS VN_TOT
,SUM(CASE WHEN SERIE='E' THEN ((-CANTIDAD-DEVOLUCIONES)*COSTO) ELSE 0 END) AS costoCre
,SUM(CASE WHEN SERIE<>'E' THEN ((-CANTIDAD-DEVOLUCIONES)*COSTO) ELSE 0 END) AS costoCon
,SUM( ((-CANTIDAD-DEVOLUCIONES)*COSTO)) AS COSTO
FROM VENTASDET_2008 
GROUP BY
YEAR,MES,LINEA  
