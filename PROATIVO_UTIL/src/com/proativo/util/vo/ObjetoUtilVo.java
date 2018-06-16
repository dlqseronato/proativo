package com.proativo.util.vo;

import java.util.List;

import com.proativo.util.enums.Segmento;

public class ObjetoUtilVo {
	protected Integer 
					id,
					resultado,
					kenanDbId;
	
	protected String 
					conta,
					ciclo,
					processamentoCiclo;
	
	public String getProcessamentoCiclo() {
		return processamentoCiclo;
	}

	public void setProcessamentoCiclo(String processamentoCiclo) {
		this.processamentoCiclo = processamentoCiclo;
	}

	protected Segmento segmento;
	
	public ObjetoUtilVo(){
		this.resultado = 1;
	}
	
	public Segmento getSegmento() {
		return segmento;
	}

	public void setSegmento(Segmento segmento) {
		this.segmento = segmento;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getResultado() {
		return resultado;
	}

	public void setResultado(Integer resultado) {
		this.resultado = resultado;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getCiclo() {
		return ciclo;
	}

	public void setCiclo(String ciclo) {
		this.ciclo = ciclo;
	}
	
	public Integer getKenanDbId() {
		return kenanDbId;
	}

	public void setKenanDbId(Integer kenanDbId) {
		this.kenanDbId = kenanDbId;
	}
	
	public String formatarListaParaString(List<?> lista){
		String resultado = "";
		
		for(int i = 0; i<lista.size();i++){
			if(i == (lista.size()-1)){
				if(lista.get(i) instanceof Integer){
					resultado += lista.get(i);
				}else{
					resultado += "'" + lista.get(i) + "'";
				}
			} else{
				if(lista.get(i) instanceof Integer){
					resultado += lista.get(i) + ",";
				}else {
					resultado += "'" + lista.get(i) + "'" + ",";
				}
			}
		}
		
		return resultado;
	}
}
