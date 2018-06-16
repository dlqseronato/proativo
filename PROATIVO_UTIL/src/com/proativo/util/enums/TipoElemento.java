package com.proativo.util.enums;

public enum TipoElemento {
	CONTA(0),
	INSTANCIA(1),
	CONTRATO(2),
	ATRELADO(3)
	;
	
	private Integer value;
	
	private TipoElemento(Integer value){
		this.value = value;
	}
	
	public Integer getValue(){
		return this.value;
	}
	
	public static TipoElemento valueOf(Integer value){
		switch(value){
		case 0:
			return TipoElemento.CONTA;
		case 1:
			return TipoElemento.INSTANCIA;
		case 2:
			return TipoElemento.CONTRATO;
		case 3:
			return TipoElemento.ATRELADO;
		default:
			return null;
		}
	}

}
