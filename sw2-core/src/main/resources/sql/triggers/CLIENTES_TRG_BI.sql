drop table if exists sx_clientes_log

CREATE TABLE `sx_clientes_log`
(
   `ID` bigint(20) NOT NULL auto_increment,
   CLIENTE_ID bigint NOT NULL,
   APELLIDOM varchar(50),
   APELLIDOP varchar(50),
   CLAVE varchar(7),
   CUENTACONTABLE varchar(30),
   CURP varchar(18),
   CALLE varchar(50),
   CIUDAD varchar(150),
   COLONIA varchar(100),
   CP varchar(6),
   ESTADO varchar(150),
   LOCALE varchar(255),
   DELMPO varchar(150),
   NUMERO varchar(10),
   NUMEROINT varchar(10),
   PAIS varchar(255),
   EMAIL3 varchar(100),
   EMAIL1 varchar(100),
   EMAIL2 varchar(100),
   FRECUENTE bit,
   NOMBRE varchar(100),
   NOMBRES varchar(150),
   PERMITIR_CHEQUE bit NOT NULL,
   FISICA bit,
   PNETO bit NOT NULL,
   RFC varchar(20),
   SUSPENDIDO bit NOT NULL,
   CREADO datetime default NULL,
   CREADO_USERID varchar(255),
   MODIFICADO datetime default NULL,
   MODIFICADO_USERID varchar(255),
   VERSION int,
   WWW varchar(250),
   CREDITO_ID bigint,
   COBRADOR_ID bigint,
   VENDEDOR_ID bigint,
   TIPO_ID bigint,
   TX_IMPORTADO datetime default NULL,
   TX_REPLICADO datetime default NULL,
	PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 ;

/** Trigger para replicacion de clientes **/
drop TRIGGER if exists CLIENTES_TRG_BI

CREATE TRIGGER CLIENTES_TRG_BI
    BEFORE INSERT ON SX_CLIENTES
    FOR EACH ROW     
    BEGIN
            DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
            SET  NEW.MODIFICADO=NOW(),NEW.CREADO=NOW();
            INSERT INTO `sx_clientes_log` (CLIENTE_ID,APELLIDOM,APELLIDOP,CLAVE,CUENTACONTABLE,CURP,CALLE,CIUDAD,COLONIA,CP
,ESTADO,LOCALE,DELMPO,NUMERO,NUMEROINT,PAIS,EMAIL3,EMAIL1,EMAIL2,FRECUENTE,NOMBRE,NOMBRES,PERMITIR_CHEQUE,FISICA,PNETO,RFC,SUSPENDIDO
,CREADO,CREADO_USERID,MODIFICADO,MODIFICADO_USERID,VERSION,WWW,CREDITO_ID,COBRADOR_ID,VENDEDOR_ID,TIPO_ID) 
        values( NEW.CLIENTE_ID
    ,NEW.APELLIDOM
    ,NEW.APELLIDOP
    ,NEW.CLAVE
    ,NEW.CUENTACONTABLE
    ,NEW.CURP
    ,NEW.CALLE
    ,NEW.CIUDAD
    ,NEW.COLONIA
    ,NEW.CP
    ,NEW.ESTADO
    ,NEW.LOCALE
    ,NEW.DELMPO
    ,NEW.NUMERO
    ,NEW.NUMEROINT
    ,NEW.PAIS
    ,NEW.EMAIL3
    ,NEW.EMAIL1
    ,NEW.EMAIL2
    ,NEW.FRECUENTE
    ,NEW.NOMBRE
    ,NEW.NOMBRES
    ,NEW.PERMITIR_CHEQUE
    ,NEW.FISICA
    ,NEW.PNETO
    ,NEW.RFC
    ,NEW.SUSPENDIDO
    ,NEW.CREADO
    ,NEW.CREADO_USERID
    ,NEW.MODIFICADO
    ,NEW.MODIFICADO_USERID
    ,NEW.VERSION
    ,NEW.WWW
    ,NEW.CREDITO_ID
    ,NEW.COBRADOR_ID
    ,NEW.VENDEDOR_ID
    ,NEW.TIPO_ID
   );
     END;

