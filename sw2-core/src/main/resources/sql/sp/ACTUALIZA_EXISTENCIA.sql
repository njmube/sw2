drop PROCEDURE ACTUALIZA_EXISTXTRD

CREATE PROCEDURE ACTUALIZA_EXISTXTRD(IN CANTIDADES (DOUBLE),IN TIPOS VARCHAR(5),IN CLAVES VARCHAR(20), IN SUCURSAL INT )
BEGIN
IF TIPOS='TPE' THEN
UPDATE sx_existencias SET CANTIDAD=CANTIDADES + (SELECT SUM(CANTIDAD) AS CANTIDAD FROM (
select 'INI' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID
,I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_ini I where clave=CLAVES AND  (I.FECHA = '2009/01/01') and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'COM' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_com I where clave=CLAVES AND  (I.FECHA > '2008/12/31')and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'MAQ' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_maq I where clave=CLAVES AND  (I.FECHA > '2008/12/31') and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'FAC' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_ventasdet I where clave=CLAVES AND  (I.FECHA > '2009/01/01')and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'DEV' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_dev I where clave=CLAVES AND  (I.FECHA > '2008/12/31') and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select I.CONCEPTO AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_mov I where clave=CLAVES AND  (I.FECHA > '2008/12/31') and sucursal_id=SUCURSAL
group by I.CONCEPTO,year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'TRS' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_trs I where clave=CLAVES AND  (I.FECHA > '2008/12/31')and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'TRD' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_trd I where clave=CLAVES AND  (I.FECHA > '2008/12/31') and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU
union
select 'KIT' AS TIPO,year(I.FECHA) AS YEAR,month(I.FECHA) AS MES,I.FECHA AS FECHA,I.SUCURSAL_ID AS SUCURSAL_ID,I.PRODUCTO_ID AS PRODUCTO_ID,
I.CLAVE AS CLAVE,I.FACTORU,sum(I.CANTIDAD) AS CANTIDAD,sum(I.KILOS) AS KILOS,I.NACIONAL AS NACIONAL from sx_inventario_kit I where clave=CLAVES AND  (I.FECHA > '2008/12/31') and sucursal_id=SUCURSAL
group by year(I.FECHA),month(I.FECHA),I.SUCURSAL_ID,I.PRODUCTO_ID,I.CLAVE,I.FECHA,I.NACIONAL,I.FACTORU) A )
where sucursal_id=SUCURSAL AND YEAR=YEAR(CURRENT_DATE) AND MES=MONTH(CURRENT_DATE) AND CLAVE=CLAVES;
END IF;
END;