
 DELIMITER $$

 CREATE TRIGGER ventasdet_log
 BEFORE INSERT ON sx_ventasdet
 FOR EACH ROW BEGIN
 SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
 END$$

 CREATE TRIGGER ventasdet_log2
 BEFORE UPDATE ON sx_ventasdet
 FOR EACH ROW BEGIN
 SET NEW.MODIFICADO=NOW();
 END$$

 DELIMITER ;









*****************SW_ACTIVOFIJO******************

CREATE TRIGGER ACTIVOFIJO_BEF_INS 
BEFORE INSERT ON SW_ACTIVO_FIJO
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END




CREATE TRIGGER ACTIVOFIJO_BEF_UPD 
BEFORE UPDATE SW_ACTIVO_FIJO
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


*******************SW_BANCOS***********************

CREATE TRIGGER BANCOS_BEF_INS 
BEFORE INSERT ON SW_BANCOS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW();
END

*******************SW_BCARGOABONO******************

CREATE TRIGGER BCARGOABONO_BEF_INS 
BEFORE INSERT ON SW_BCARGOABONO
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER BCARGOABON0_BEF_UPD 
BEFORE UPDATE ON SW_BCARGOABONO
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

******************SW_CONSIGNATARIOS****************

CREATE TRIGGER CONSIGNATARIOS_BEF_INS 
BEFORE INSERT ON SW_CONSIGNATARIOS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER CONSIGNATARIOS_BEF_UPD 
BEFORE UPDATE ON SW_CONSIGNATARIOS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


******************* SW_GPROVEEDOR*********************

CREATE TRIGGER GPROVEEDOR_BEF_INS 
BEFORE INSERT ON SW_GPROVEEDOR
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER GPROVEEDOR_BEF_UPD 
BEFORE UPDATE ON SW_GPROVEEDOR
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


******************SW_IPC********************

CREATE TRIGGER IPC_BEF_INS 
BEFORE INSERT ON SW_IPC
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER IPC_BEF_UPD 
BEFORE UPDATE ON SW_IPC
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


****************SW_TPAGO*********************

CREATE TRIGGER TPAGO_BEF_INS 
BEFORE INSERT ON SW_TPAGO
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER TPAGO_BEF_UPD 
BEFORE UPDATE ON SW_TPAGO
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*****************TRANSFERENCIAS*****************

CREATE TRIGGER TRANSFERENCIAS_BEF_INS 
BEFORE INSERT ON SW_TRANSFERENCIAS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER TRANSFERENCIAS_BEF_UPD 
BEFORE UPDATE ON SW_TRANSFERENCIAS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*****************SW_TREQUISICION****************


CREATE TRIGGER TRANSFERENCIAS_BEF_INS 
BEFORE INSERT ON SW_TRANSFERENCIAS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER TRANSFERENCIAS_BEF_UPD 
BEFORE UPDATE ON SW_TRANSFERENCIAS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***************SW_TREQUISICIONDET****************

CREATE TRIGGER TREQUISICIONDET_BEF_INS 
BEFORE INSERT ON SW_TREQUISICIONDET
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER TREQUISICIONDET_BEF_UPD 
BEFORE UPDATE ON SW_TREQUISICIONDET
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***************SX_AUTORIZACIONES2****************

CREATE TRIGGER AUTORIZACIONES2_BEF_INS 
BEFORE INSERT ON SX_AUTORIZACIONES2
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER AUTORIZACIONES2_BEF_UPD 
BEFORE UPDATE ON SX_AUTORIZACIONES2
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

**************SX_CLIENTES*************************

CREATE TRIGGER CLIENTES_BEF_INS 
BEFORE INSERT ON SX_CLIENTES
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER CLIENTES_BEF_UPD 
BEFORE UPDATE ON SX_CLIENTES
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*****************SX_COBRADORES*********************

CREATE TRIGGER COBRADORES_BEF_INS 
BEFORE INSERT ON SX_COBRADORES
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER COBRADORES_BEF_UPD 
BEFORE UPDATE ON SX_COBRADORES
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***************SX_COMPRAS***************************

CREATE TRIGGER COMPRAS_BEF_INS 
BEFORE INSERT ON SX_COMPRAS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER COMPRAS_BEF_UPD 
BEFORE UPDATE ON SX_COMPRAS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

**************SX_COMPRASDET****************************

CREATE TRIGGER COMPRASDET_BEF_INS 
BEFORE INSERT ON SX_COMPRASDET
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER COMPRASDET_BEF_UPD 
BEFORE UPDATE ON SX_COMPRASDET
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

************SX_CXC_ABONOS****************************

CREATE TRIGGER CXCABONOS_BEF_INS 
BEFORE INSERT ON SX_CXC_ABONOS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER CXCABONOS_BEF_UPD 
BEFORE UPDATE ON SX_CXC_ABONOS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*************SX_CXC_APLICACIONES**********************

CREATE TRIGGER CXCAPLICACIONES_BEF_INS 
BEFORE INSERT ON SX_CXC_APLICACIONES
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER CXCAPLICACIONES_BEF_UPD 
BEFORE UPDATE ON SX_CXC_APLICACIONES
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

****************SX_CXP***********************************

CREATE TRIGGER CXP_BEF_INS 
BEFORE INSERT ON SX_CXP
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER CXP_BEF_UPD 
BEFORE UPDATE ON SX_CXP
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


******************SX_DESC_VOL************************

CREATE TRIGGER DESC_VOL_BEF_INS 
BEFORE INSERT ON SX_DESC_VOL
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW();
END

****************SX_DESC_VOL_CLIE*********************

CREATE TRIGGER DESCVOLCLIE_BEF_INS 
BEFORE INSERT ON SX_DESC_VOL_CLIE
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW();
END

****************SX_DEVOLUCIONES**********************


CREATE TRIGGER DEVOLUCIONES_BEF_INS 
BEFORE INSERT ON SX_DEVOLUCIONES
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER DEVOLUCIONES_BEF_UPD 
BEFORE UPDATE ON SX_DEVOLUCIONES
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

****************SX_EXISTENCIAS*************************

CREATE TRIGGER EXISTENCIAS_BEF_INS 
BEFORE INSERT ON SX_EXISTENCIAS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER EXISTENCIAS_BEF_UPD 
BEFORE UPDATE ON SX_EXISTENCIAS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


****************SX_INVENTARIO_COM*************************

CREATE TRIGGER INVENTARIOCOM_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_COM
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOCOM_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_COM
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***********************SX_INVENTARIO_DEV*************************

CREATE TRIGGER INVENTARIODEV_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_DEV
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIODEV_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_DEV
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*********************SX_INVENTARIO_FAC************************

CREATE TRIGGER INVENTARIOFAC_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_FAC
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOFAC_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_FAC
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*******************SX_INVENTARIO_INI**************************

CREATE TRIGGER INVENTARIOINI_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_INI
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOINI_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_INI
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

************SX_INVENTARIO_KIT*************************************

CREATE TRIGGER INVENTARIOKIT_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_KIT
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOKIT_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_KIT
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


***********************SX_INVENTARIO_MAQ***********************

CREATE TRIGGER INVENTARIOMAQ_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_MAQ
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOMAQ_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_MAQ
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*********************SX_INVENTARIO_MOV************************

CREATE TRIGGER INVENTARIOMOV_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_MOV
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOMOV_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_MOV
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***********************SX_INVENTARIO_TPE************************

CREATE TRIGGER INVENTARIOTPE_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_TPE
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOTPE_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_TPE
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

**********************SX_INVENTARIO_TPS************************

CREATE TRIGGER INVENTARIOTPS_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_TPS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOTPS_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_TPS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

********************SX_INVENTARIO_TRD******************************

CREATE TRIGGER INVENTARIOTRD_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_TRD
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOTRD_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_TRD
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***************SX_INVENTARIO_TRS*********************************

CREATE TRIGGER INVENTARIOTRS_BEF_INS 
BEFORE INSERT ON SX_INVENTARIO_TRS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER INVENTARIOTRS_BEF_UPD 
BEFORE UPDATE ON SX_INVENTARIO_TRS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*****************SX_LP_CLIENTE************************************

CREATE TRIGGER LPCLIENTE_BEF_INS 
BEFORE INSERT ON SX_LP_CLIENTE
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER LPCLIENTE_BEF_UPD 
BEFORE UPDATE ON SX_LP_CLIENTE
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


*****************SX_LP_CLIENTE_DET**********************************

CREATE TRIGGER LPCLIENTEDET_BEF_INS 
BEFORE INSERT ON SX_LP_CLIENTE_DET
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER LPCLIENTEDET_BEF_UPD 
BEFORE UPDATE ON SX_LP_CLIENTE_DET
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*******************SX_LP_PROVS***************************************

CREATE TRIGGER LPPROVS_BEF_INS 
BEFORE INSERT ON SX_LP_PROVS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER LPPROVS_BEF_UPD 
BEFORE UPDATE ON SX_LP_PROVS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

******************SX_LP_VENT*********************************************

CREATE TRIGGER LPVENT_BEF_INS 
BEFORE INSERT ON SX_LP_VENT
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER LPVENT_BEF_UPD 
BEFORE UPDATE ON SX_LP_VENT
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***********************SX_MOVI*************************************

CREATE TRIGGER MOVI_BEF_INS 
BEFORE INSERT ON SX_MOVI
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER MOVI_BEF_UPD 
BEFORE UPDATE ON SX_MOVI
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


*******************SX_PRODUCTOS************************************

CREATE TRIGGER MOVI_BEF_INS 
BEFORE INSERT ON SX_MOVI
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER MOVI_BEF_UPD 
BEFORE UPDATE ON SX_MOVI
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


**************************SX_PROVEEDORES**************************

CREATE TRIGGER PROVEEDORES_BEF_INS 
BEFORE INSERT ON SX_PROVEEDORES
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER PROVEEDORES_BEF_UPD 
BEFORE UPDATE ON SX_PROVEEDORES
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***********************SX_RECEPCION_MAQUILA***********************

CREATE TRIGGER RECEPCIONMAQUILA_BEF_INS 
BEFORE INSERT ON SX_RECEPCION_MAQUILA
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER RECEPCIONMAQUILA_BEF_UPD 
BEFORE UPDATE ON SX_RECEPCION_MAQUILA
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***********************SX_TRASLADOS***********************************

CREATE TRIGGER TRASLADOS_BEF_INS 
BEFORE INSERT ON SX_TRASLADOS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER TRASLADOS_BEF_UPD 
BEFORE UPDATE ON SX_TRASLADOS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***************************SX_TRS****************************************

CREATE TRIGGER TRS_BEF_INS 
BEFORE INSERT ON SX_TRS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER TRS_BEF_UPD 
BEFORE UPDATE ON SX_TRS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

**************************SX_VENDEDORES*****************************

CREATE TRIGGER VENDEDORES_BEF_INS 
BEFORE INSERT ON SX_VENDEDORES
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER VENDEDORES_BEF_UPD 
BEFORE UPDATE ON SX_VENDEDORES
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

**********************SX_VENTAS**************************************

CREATE TRIGGER VENTAS_BEF_INS 
BEFORE INSERT ON SX_VENTAS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER VENTAS_BEF_UPD 
BEFORE UPDATE ON SX_VENTAS
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*********************SX_VENTAS_COMISION_COB***************************

CREATE TRIGGER VENTASCOMISIONCOB_BEF_INS 
BEFORE INSERT ON SX_VENTAS_COMISION_COB
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER VENTASCOMISIONCOB_BEF_UPD 
BEFORE UPDATE ON SX_VENTAS_COMISION_COB
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

*******************SX_VENTAS_COMISON_VEN****************************

CREATE TRIGGER VENTASCOMISIONVEN_BEF_INS 
BEFORE INSERT ON SX_VENTAS_COMISION_VEN
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER VENTASCOMISIONVEN_BEF_UPD 
BEFORE UPDATE ON SX_VENTAS_COMISION_VEN
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()


***********************SX_VENTASDET**************************


CREATE TRIGGER VENTASDET_BEF_INS 
BEFORE INSERT ON SX_VENTASDET
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW();
END



CREATE TRIGGER VENTASDET_BEF_UPD 
BEFORE UPDATE ON SX_VENTASDET
FOR EACH ROW 
SET  NEW.MODIFICADO=NOW()

***********************SX_CXC_ABONOS_CANCELADOS**************************


CREATE TRIGGER CXC_ABONOS_CANCELADOS_BEF_INS
BEFORE INSERT ON SX_CXC_ABONOS_CANCELADOS
FOR EACH ROW BEGIN
SET NEW.CREADO=NOW(), NEW.MODIFICADO=NOW(), NEW.FECHA=NOW();
END


CREATE TRIGGER CXC_ABONOS_CANCELADOS_BEF_UPD
BEFORE UPDATE ON SX_CXC_ABONOS_CANCELADOS
FOR EACH ROW
SET  NEW.MODIFICADO=NOW();







