package com.proativo.util.vo;

import java.sql.Timestamp;
import java.util.List;

public class ProcessamentoCicloVo {

	private String 	processamentoCiclo;
	
	private List<String> ciclos;
	
	private Timestamp 	dataCorte,
						dataCorteAnterior;
	
	private String[] parametros;
	
	public ProcessamentoCicloVo(String processamentoCiclo, Timestamp dataCorte, Timestamp dataCorteAnterior){
		this.processamentoCiclo = processamentoCiclo;
		this.dataCorte = dataCorte;
		this.dataCorteAnterior = dataCorteAnterior;
	}
	
	public ProcessamentoCicloVo(String processamentoCiclo){
		this.processamentoCiclo = processamentoCiclo;
	}
	
	public ProcessamentoCicloVo(){
	}
	
	public Timestamp getDataCorte() {
		return dataCorte;
	}
	public Timestamp getDataCorteAnterior() {
		return dataCorteAnterior;
	}

	public String getProcessamentoCiclo() {
		return processamentoCiclo;
	}

	public void setProcessamentoCiclo(String processamentoCiclo) {
		this.processamentoCiclo = processamentoCiclo;
	}

	public List<String> getCiclos() {
		return ciclos;
	}

	public void setCiclos(List<String> ciclos) {
		this.ciclos = ciclos;
	}
	
    public String buscarCiclosFormatado(){
		String resultado = "";
		
		for(int i = 0; i<ciclos.size();i++){
			if(i == (ciclos.size()-1)){
				resultado += "'" + ciclos.get(i) + "'";
			} else{
				resultado += "'" + ciclos.get(i) + "'" + ",";
			}
		}
		
		return resultado;
    }
    
    public String buscarCiclosFormatadoSiebel(){
		String resultado = "";
		
		for(int i = 0; i<ciclos.size();i++){
			if(i == (ciclos.size()-1)){
				resultado += Integer.parseInt(ciclos.get(i).substring(1));
			} else{
				resultado += Integer.parseInt(ciclos.get(i).substring(1)) + ",";
			}
		}
		
		return resultado;
    }

	public String[] getParametros() {
		return parametros;
	}

	public void setParametros(String[] parametros) {
		this.parametros = parametros;
	}
}
