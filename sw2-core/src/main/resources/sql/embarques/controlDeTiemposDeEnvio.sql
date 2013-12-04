SELECT V.CARGO_ID,V.CLAVE,V.NOMBRE,(SELECT S.NOMBRE FROM SW_SUCURSALES S WHERE S.SUCURSAL_ID=V.SUCURSAL_ID) AS SUC,P.FOLIO,V.DOCTO,V.ORIGEN,CASE WHEN V.CE IS TRUE THEN 'SI' ELSE '' END AS COD
,Q.DOCUMENTO AS ENVIO,Q.TRANSPORTE_ID,Q.CHOFER,E.KILOS,CASE WHEN E.PARCIAL IS TRUE THEN 'SI' ELSE '' END AS PARCIAL ,P.CREADO AS PEDIDO
,Q.SALIDA AS SAL_FECHA
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(V.CREADO)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_FAC
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS FACTURADO
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(V.CREADO)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,V.CREADO,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_FAC
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.SURTIDO)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_SRT
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS SURTIDO
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.SURTIDO)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.SURTIDO,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_SRT
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.CREADO)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_ASG
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS ASIGNADO
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.CREADO)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.CREADO,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_ASG
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(Q.SALIDA)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_SAL
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS SALIDA
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(Q.SALIDA)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.SALIDA,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_SAL
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.ARRIBO)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_ARR
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS ARRIBO
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.ARRIBO)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.ARRIBO,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_ARR
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.RECEPCION)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_RCP
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS RECEPCION
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(E.RECEPCION)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,E.RECEPCION,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_RCP
--
,FLOOR(CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(Q.REGRESO)-1)<4 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END)-1 
		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H')<168 THEN (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END)
		ELSE (CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END)-(FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END))/7) 
	END) AS DIA_RGR
,TIME_FORMAT((SEC_TO_TIME(((MOD(-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60,24)
  -(CASE 	WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 0
  		WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)>=1 AND TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H')>9.5 THEN 14.5
  		WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H')>24 THEN 14.5
  		ELSE 0 END)
  )*3600))),'%H:%i') AS REGRESO
,ROUND((CASE 	WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H') BETWEEN 24 AND 168 AND (DAYOFWEEK(P.CREADO)-1)>=4  AND (DAYOFWEEK(Q.REGRESO)-1)<4 
				THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H:%i')-
				(CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) BETWEEN 1 AND 2 AND (DAYOFWEEK(P.CREADO)-1)=6 THEN 14.5 ELSE 28.5 END)
				-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END)))*14.5)
			WHEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H')<168 THEN TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END)))*14.5)
		ELSE TIME_FORMAT((SEC_TO_TIME((-TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)*3600)),'%H:%i')-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END))/7)*13.5)-FLOOR((FLOOR((CASE WHEN FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24)<=0 THEN 0 ELSE FLOOR(-(TIMESTAMPDIFF(MINUTE,Q.REGRESO,P.CREADO)/60)/24) END)))*14.5)
	END),1) AS HRS_RGR
--
,V.TOTAL,V.TOTAL-IFNULL((SELECT SUM(A.IMPORTE) FROM sx_cxc_aplicaciones A WHERE A.CARGO_ID=V.CARGO_ID AND A.FECHA<='2013/09/30'),0) AS SALDO
,(SELECT MAX(A.CREADO) FROM sx_cxc_aplicaciones A WHERE A.CARGO_ID=V.CARGO_ID AND A.FECHA<='2013/09/30') AS ULT_PAGO
,(SELECT MIN(X.CREADO) FROM SX_EMBARQUES X WHERE X.TRANSPORTE_ID=Q.TRANSPORTE_ID AND X.FECHA='2013/09/30') AS FECHA_EMB
FROM sx_ventas v
join sx_pedidos p on(p.PEDIDO_ID=v.PEDIDO_ID)
join sx_entregas e on(v.CARGO_ID=e.VENTA_ID)
join sx_embarques q on(q.EMBARQUE_ID=e.EMBARQUE_ID) 
where date(Q.CREADO) BETWEEN '2013/09/30' and '2013/09/30' AND V.INSTRUCCION_ENTREGA IS NOT NULL 
