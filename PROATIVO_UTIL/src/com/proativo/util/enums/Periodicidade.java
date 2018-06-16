package com.proativo.util.enums;

public enum Periodicidade {
	DIARIA(1),
	CICLO(2);
	
	private Integer value;
	
	private Periodicidade(Integer value){
		this.value = value;
	}
	
	public Integer getValue(){
		return this.value;
	}
	
	public static Periodicidade valueOf(Integer value){
		switch(value){
		case 1:
			return Periodicidade.DIARIA;
		case 2:
			return Periodicidade.CICLO;
		default:
			return null;
		}
	}
}
