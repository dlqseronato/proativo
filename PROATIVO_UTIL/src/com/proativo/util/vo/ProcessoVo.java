/*
 * ProcessoVO.java 30/03/2011
 * Direitos autorais 2011 Global Village Telecom, Inc. Todos direiros reservados.
 * GVT Proprietaria confidêncial. O uso está sujeito aos termos da lincença.
 */
package com.proativo.util.vo;

/**
 * Classe de atribuição dos processos.
 * @author G0015659 - Vinicius de Souza
 *
 */
public class ProcessoVo {
	private String classe;
	private int periodo, id;
	private String aplicacao;
	
	public ProcessoVo(String aplicacao) {
		this.aplicacao = aplicacao;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getClasse() {
		return classe;
	}
	
	public void setClasse(String classe) {
		this.classe = classe;
	}
	
	public int getPeriodo() {
		return periodo;
	}
	
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public String getAplicacao() {
		return aplicacao;
	}
}