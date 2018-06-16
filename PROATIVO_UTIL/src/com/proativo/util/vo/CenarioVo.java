package com.proativo.util.vo;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.proativo.util.enums.Periodicidade;
import com.proativo.util.enums.TipoExecucao;

public class CenarioVo {
	
	public CenarioVo(String idCenario){
		this.cenario = idCenario;
	}

	private Integer idCenario;
	private String cenario;
	private String nomeCenario;
	private Timestamp dataInativacao;
	
	// Id único da execução
	private Integer idExecucao;
	
	// Levantamento ou Tratamento
	private TipoExecucao tipoExecucao;
	
	// Diario ou Ciclo
	private Periodicidade periodicidade;
	
	// 1 ativo, 0 inativo
	private Boolean executavel;
	
	private Boolean contingencia;

	// Para processamento avulso
	private String processamentoCiclo;
	
	private Integer quantidadeThreads;
	
	private Integer quantidadeErros;
	
	private File sqlUtil;
	
	private File diretorioContingencia;
	
	private Integer relatorio;

	private String segmento;
	private String segmentosKenan;
	
	private Integer progresso;
	
	private Integer gerarScript;
	
	private List<File> arquivosGerados;
	
	private List<String> emailPara;
	
	private List<String> emailCc;
	
	private List<String> emailLog;
	
	public List<String> getEmailPara() {
		return emailPara;
	}

	public void setEmailPara(List<String> emailPara) {
		this.emailPara = emailPara;
	}

	public List<String> getEmailCc() {
		return emailCc;
	}

	public void setEmailCc(List<String> emailCc) {
		this.emailCc = emailCc;
	}

	public List<String> getEmailLog() {
		return emailLog;
	}

	public void setEmailLog(List<String> emailLog) {
		this.emailLog = emailLog;
	}

	public CenarioVo(){
		arquivosGerados = new ArrayList<File>();
	}
	
	public File getSqlUtil() {
		return sqlUtil;
	}

	public void setSqlUtil(File sqlUtil) {
		this.sqlUtil = sqlUtil;
	}

	public Integer getQuantidadeThreads() {
		return quantidadeThreads;
	}

	public void setQuantidadeThreads(Integer quantidadeThreads) {
		this.quantidadeThreads = quantidadeThreads;
	}

	public Integer getQuantidadeErros() {
		return quantidadeErros;
	}

	public void setQuantidadeErros(Integer quantidadeErros) {
		this.quantidadeErros = quantidadeErros;
	}

	public Boolean getExecutavel() {
		return executavel;
	}

	public Boolean getContingencia() {
		return contingencia;
	}

	public String getCenario() {
		return cenario;
	}

	public void setCenario(String cenario) {
		this.cenario = cenario;
	}

	public String getNomeCenario() {
		return nomeCenario;
	}

	public void setNomeCenario(String nomeCenario) {
		this.nomeCenario = nomeCenario;
	}


	public Periodicidade getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(Periodicidade periodicidade) {
		this.periodicidade = periodicidade;
	}

	public Boolean isExecutavel() {
		return executavel;
	}

	public void setExecutavel(Boolean executavel) {
		this.executavel = executavel;
	}

	public Boolean isContingencia() {
		return contingencia;
	}

	public void setContingencia(Boolean contingencia) {
		this.contingencia = contingencia;
	}

	public String getProcessamentoCiclo() {
		return processamentoCiclo;
	}

	public void setProcessamentoCiclo(String processamentoCiclo) {
		this.processamentoCiclo = processamentoCiclo;
	}

	public TipoExecucao getTipoExecucao() {
		return tipoExecucao;
	}

	public void setTipoExecucao(TipoExecucao tipoExecucao) {
		this.tipoExecucao = tipoExecucao;
	}

	public File getDiretorioContingencia() {
		return diretorioContingencia;
	}

	public void setDiretorioContingencia(File diretorioContingencia) {
		this.diretorioContingencia = diretorioContingencia;
	}

	public Integer getIdExecucao() {
		return idExecucao;
	}

	public void setIdExecucao(Integer idExecucao) {
		this.idExecucao = idExecucao;
	}

	public Integer getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(Integer relatorio) {
		this.relatorio = relatorio;
	}

	public String getSegmento() {
		return segmento;
	}

	public void setSegmento(String segmento) {
		this.segmento = segmento;
	}

	public Integer getIdCenario() {
		return idCenario;
	}

	public void setIdCenario(Integer idCenario) {
		this.idCenario = idCenario;
	}

	public Integer getProgresso() {
		return progresso;
	}

	public void setProgresso(Integer progresso) {
		this.progresso = progresso;
	}

	public Integer getGerarScript() {
		return gerarScript;
	}

	public void setGerarScript(Integer gerarScript) {
		this.gerarScript = gerarScript;
	}

	public List<File> getArquivosGerados() {
		return arquivosGerados;
	}

	public void setArquivosGerados(List<File> arquivosGerados) {
		this.arquivosGerados = arquivosGerados;
	}

	public String getSegmentosKenan() {
		return segmentosKenan;
	}

	public void setSegmentosKenan(String segmentosKenan) {
		this.segmentosKenan = segmentosKenan;
	}

	public Timestamp getDataInativacao() {
		return dataInativacao;
	}

	public void setDataInativacao(Timestamp dataInativacao) {
		this.dataInativacao = dataInativacao;
	}
	
}
