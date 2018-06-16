package com.proativo.util.vo;

import java.sql.Timestamp;
import java.util.List;

public class ServicoVo {
	private Integer subscrNo;
	private String instancia;
	private Integer tipoInstancia;
	private Timestamp dataDesconexao;
	private Timestamp dataAtivacao;
	private List<ComponentVo> componentes;
	
	public Integer getSubscrNo() {
		return subscrNo;
	}
	public void setSubscrNo(Integer subscrNo) {
		this.subscrNo = subscrNo;
	}
	public String getInstancia() {
		return instancia;
	}
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}
	public Integer getTipoInstancia() {
		return tipoInstancia;
	}
	public void setTipoInstancia(Integer tipoInstancia) {
		this.tipoInstancia = tipoInstancia;
	}
	public Timestamp getDataDesconexao() {
		return dataDesconexao;
	}
	public void setDataDesconexao(Timestamp dataDesconexao) {
		this.dataDesconexao = dataDesconexao;
	}
	public List<ComponentVo> getComponentes() {
		return componentes;
	}
	public void setComponentes(List<ComponentVo> componentes) {
		this.componentes = componentes;
	}
	public Timestamp getDataAtivacao() {
		return dataAtivacao;
	}
	public void setDataAtivacao(Timestamp dataAtivacao) {
		this.dataAtivacao = dataAtivacao;
	}
}
