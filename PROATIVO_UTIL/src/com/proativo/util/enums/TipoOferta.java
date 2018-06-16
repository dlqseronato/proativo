package com.proativo.util.enums;

public enum TipoOferta {
	LOREN("Loren"),
	CATARINA("Catarina"),
	FRIDA("Frida"),
	TOUCHE("Touche"),
	LEGADO("Legado"),
	NENHUMA("nehuma");
	;
	
	private String value;
	
	private TipoOferta(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public static TipoOferta equals(String value){
		switch(value){
		case "Loren":
			return TipoOferta.LOREN;
		case "Catarina":
			return TipoOferta.CATARINA;
		case "Frida":
			return TipoOferta.FRIDA;
		case "Touche":
			return TipoOferta.TOUCHE;
		case "Legado":
			return TipoOferta.LEGADO;
		default:
			return null;
		}
	}
}
