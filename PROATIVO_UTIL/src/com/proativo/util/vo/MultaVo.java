package com.proativo.util.vo;

import java.sql.Timestamp;
import java.util.List;

public class MultaVo {
	private String tipo;
	private Double valor;
	private Timestamp dataCriacao;
	private boolean possuiMulta;
	private List<TipoNrc> tiposNrc;
	
	public MultaVo(){
		this.possuiMulta = false;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Timestamp getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Timestamp dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public boolean isPossuiMulta() {
		return possuiMulta;
	}
	public void setPossuiMulta(boolean possuiMulta) {
		this.possuiMulta = possuiMulta;
	}

	public List<TipoNrc> getTiposNrc() {
		return tiposNrc;
	}

	public void setTiposNrc(List<TipoNrc> tiposNrc) {
		this.tiposNrc = tiposNrc;
	}
}
