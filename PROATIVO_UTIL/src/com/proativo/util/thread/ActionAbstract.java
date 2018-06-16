package com.proativo.util.thread;

import java.text.NumberFormat;

import com.proativo.util.log.Log;
import com.proativo.util.vo.AbstractBeanVo;
import com.proativo.util.vo.CenarioVo;


/**
 * Classe de execução de processos. Está classe é usualmente utilizada para interar em forma de Threads Beans definidos pela aplicação.
 * @author G0015659 - Vinicius de Souza.
 *
 * @param <T> Objeto incluso na lista passada ao ThreadManager.
 * 
 * @see AbstractBeanVo
 */
public abstract class ActionAbstract<T extends Object> {
	protected ThreadManagerDynamicConnection tmdc;
	private Float qtde;
	private Float percentualAnalisado;
	protected Float totalLista;
	private Integer percentualAux;
	private NumberFormat formatador;
	protected CenarioVo cenario;
	
	/**
	 * Método a ser chamado durante a execução em concorrência.
	 * @param vo Value Object da lista passada ao ThreadManager.
	 */
	public abstract void exec(T vo);
	
	public ActionAbstract(){
		this.qtde = 0f;
		this.percentualAnalisado = 0f;
		this.percentualAux = 0;
		this.formatador = NumberFormat.getInstance();
		
	}
	
	protected synchronized void atualizarProgresso(){
		try{
			percentualAnalisado = ((++qtde/totalLista)*100);
			if(percentualAnalisado - percentualAux >= 10){
				percentualAux += 10;
				cenario.setProgresso(percentualAnalisado.intValue());
				Log.info("Processados " + formatador.format(qtde.intValue()) + " objetos de " + formatador.format(totalLista.intValue()) + " - " +  percentualAnalisado.intValue() + "%");
			}
		}catch(Exception e){
			Log.error("Erro ao atualizar progresso", e);
		}
	}
	
	protected synchronized void atualizarProgresso(Integer numero){
		try{
			percentualAnalisado = ((++qtde/totalLista)*100);
			if(percentualAnalisado - percentualAux >= numero){
				percentualAux += numero;
				cenario.setProgresso(percentualAnalisado.intValue());
				Log.info("Processados " + formatador.format(qtde.intValue()) + " objetos de " + formatador.format(totalLista.intValue()) + " - " +  percentualAnalisado.intValue() + "%");
			}
		}catch(Exception e){
			Log.error("Erro ao atualizar progresso", e);
		}
	}
}