package com.proativo.util.enums;

public enum Segmento {
	SME(9),
	RESIDENCIAL(10),
	RETAIL(11),
	CORPORATE(12),
	GRANDE_EMPRESA(13),
	ATACADISTA(14),
	GOVERNO(15),
	ESCRITORIO_GVT(16),
	CLIENTES_NAO_GVT(17),
	CLIENTES_NAO_GVT_BRT(18),
	CLIENTES_NAO_GVT_CTBC(19),
	CLIENTES_NAO_GVT_SERCOMTEL(20),
	AUTARQUIA_FEDERAL(21),
	CALL_CENTER(22);
	
	private Integer value;
	
	private Segmento(Integer value){
		this.value = value;
	}
	
	public Integer getValue(){
		return this.value;
	}
	
	public static Segmento valueOf(Integer value){
		switch(value){
		case 9:
			return Segmento.SME;
		case 10:
			return Segmento.RESIDENCIAL;
		case 11:
			return Segmento.RETAIL;
		case 12:
			return Segmento.CORPORATE;
		case 13:
			return Segmento.GRANDE_EMPRESA;
		case 14:
			return Segmento.ATACADISTA;
		case 15:
			return Segmento.GOVERNO;
		case 16:
			return Segmento.ESCRITORIO_GVT;
		case 17:
			return Segmento.CLIENTES_NAO_GVT;
		case 18:
			return Segmento.CLIENTES_NAO_GVT_BRT;
		case 19:
			return Segmento.CLIENTES_NAO_GVT_CTBC;
		case 20:
			return Segmento.CLIENTES_NAO_GVT_SERCOMTEL;
		case 21:
			return Segmento.AUTARQUIA_FEDERAL;
		case 22:
			return Segmento.CALL_CENTER;
		default:
			return null;
		}
	}
}
