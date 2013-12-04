package com.luxsoft.siipap.cxc.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public enum TiposDeNotas {
	
	C("Bonificacion Camioneta"),
	Y("Bonificaci�n Jur�dico"),
	F("Bonificaci�n Mostrador"),
	L("Nota de Cr�dito por bonificaci�n"),
	H("Devoluci�n Mostrador"),
	I("Devoluci�n Camioneta"),
	J("Devoluci�n Cr�dito"),
	T("Descuento Cr�dito Dic"),
	U("Descuento Cr�dito 1"),
	V("Descuento dos"),
	W("Descuento adicional"),
	M("Nota de Cargo Cred"),
	Q("Nota de Cargo Cam"),
	O("Nota de Cargo Cheq Dev"),
	P("Nota de Cargo Jur�dico"),
	Z("Nota de Cargo Choferes")
	;
	
	
	
	private String desc;
	
	private TiposDeNotas( String desc) {		
		this.desc = desc;
	}
	
	
	public String getDesc() {
		return MessageFormat.format(desc+"  ({0})  ", name());
	}
	
	public static List<String> getStringIds(){
		List<String> ids=new ArrayList<String>();
		for(TiposDeNotas t:values()){
			ids.add(t.name());
		}
		return ids;
	}
	
}

