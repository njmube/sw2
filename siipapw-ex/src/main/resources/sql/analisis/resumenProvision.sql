SELECT A.YEAR,A.MES
,-SUM(ROUND((A.SUBTOTAL-NVL((SELECT SUM(-X.IMPORTE/1.15) FROM SW_NOTASDET X WHERE X.VENTA_ID=A.VENTA_ID AND X.TIPO='J' AND X.FECHA<='@FECHA' ),0) ),2)
*(NVL(ROUND(((SELECT X.DESCUENTO FROM SW_VENTASCREDITO X WHERE X.VENTA_ID=A.VENTA_ID AND A.TIPO IN('E','S') 
     AND X.VENTA_ID NOT IN((SELECT XX.VENTA_ID FROM SW_NOTASDET XX WHERE XX.TIPO IN('U') AND XX.VENTA_ID=A.VENTA_ID AND XX.FECHA<='@FECHA' )) )),2),0))/100) AS IMP_PROV
FROM V_VENTAS A 
WHERE  A.YEAR>=? 
AND A.ORIGEN='CRE' AND A.TIPO IN('E','S')
AND ROUND((A.TOTAL-NVL((SELECT SUM(-X.IMPORTE) FROM SW_NOTASDET X WHERE X.VENTA_ID=A.VENTA_ID AND X.TIPO<>'M' AND X.FECHA<='@FECHA' AND X.NOTA_ID IN((SELECT Y.NOTA_ID FROM SW_NOTAS Y WHERE Y.APLICABLE=0 AND  Y.NOTA_ID=X.NOTA_ID AND X.FECHA<='@FECHA'))),0)
   -NVL((SELECT SUM(X.IMPORTE) FROM SW_PAGOS X WHERE X.VENTA_ID=A.VENTA_ID AND X.FECHA<='@FECHA'  AND FORMADEPAGO NOT IN('K','U')),0) ),2)>0.1
AND NVL(ROUND(((SELECT X.DESCUENTO FROM SW_VENTASCREDITO X WHERE X.VENTA_ID=A.VENTA_ID AND A.TIPO IN('E','S') 
     AND X.VENTA_ID NOT IN((SELECT XX.VENTA_ID FROM SW_NOTASDET XX WHERE XX.TIPO IN('U','L') AND XX.VENTA_ID=A.VENTA_ID AND XX.FECHA<='@FECHA' )) )),2),0)>0
GROUP BY A.YEAR,A.MES