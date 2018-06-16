package com.proativo.util.enums;

public enum TipoExecucao {
	LEVANTAMENTO(1),
	LEVANTAMENTO_E_TRATAMENTO(2),
	ALARME(3);
	
	private Integer value;
	
	private TipoExecucao(Integer value){
		this.value = value;
	}
	
	public Integer getValue(){
		return this.value;
	}
	
	public static TipoExecucao valueOf(Integer value){
		switch(value){
		case 1:
			return TipoExecucao.LEVANTAMENTO;
		case 2:
			return TipoExecucao.LEVANTAMENTO_E_TRATAMENTO;
		case 3:
			return TipoExecucao.ALARME;
		default:
			return null;
		}
	}
}
