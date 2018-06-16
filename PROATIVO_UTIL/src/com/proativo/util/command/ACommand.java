/*
k * ACommand.java 30/03/2011
 * Direitos autorais 2011 Global Village Telecom, Inc. Todos direiros reservados.
 * GVT Proprietaria confidêncial. O uso está sujeito aos termos da lincença.
 */
package com.proativo.util.command;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Observer;

import com.proativo.util.Processo;
import com.proativo.util.log.Log;
import com.proativo.util.vo.ProcessoVo;

/**
 * Classe que gerencia a execução dos processo de uma aplicação.
 * @author G0015659 - Vinicius de Souza
 *
 */
public abstract class ACommand implements Observer {
	/**
	 * Atributo que armazena a quantidade de threads que estão ativas.
	 */
	protected int threadsNumber;
	
	/**
	 * Método de execução dos processos.
	 * @param processos Lista de processos a serem executados.
	 */
	public abstract void execute(List<ProcessoVo> processos);
	
	/**
	 * Método que retorna o nome do processo em execução.
	 * 
	 * @return Nome do processo que está sendo executado, podendo ser 'Load', 'Action', 'Validation' ou 'Report' 
	 */
	public abstract String getProcesso();
	
	/**
	 * Método que retorna o caminho do pacote para as classes de negócio.
	 * @return Estrutura de pacotes.
	 */
	public abstract String getPackageName();
	
	/**
	 * Método chamado pelas subclasses para iniciar as threads dos processos.
	 * @param cenarios Lista de processos a serem executados.
	 * @return Retorna verdadeiro caso o processos seja executado com sucesso.
	 */
	protected boolean launchClass(List<ProcessoVo> processos) {
		boolean result = false;
		String className = null;
		
		try {
			for (ProcessoVo processo : processos) {
				className = this.getClassName(processo.getClasse()); 

				Log.info("Iniciando cenário: " + className);
				Processo.start("bss_log_processos", processo.getAplicacao(), className);

				result = this.launchClass(className, processo.getPeriodo());
			}	
		} catch (ClassNotFoundException e) {
			Log.error("Classe: " + className + ". Não encontrada", e);
		} catch (IllegalArgumentException e) {
			Log.error("Classe: " + className + ". Construtor da classe não é público", e);
		} catch (SecurityException e) {
			Log.error("Classe: " + className + ". Premissão não consedida", e);
		} catch (InstantiationException e) {
			Log.error("Classe: " + className + ". Não é possível de instanciar", e);
		} catch (IllegalAccessException e) {
			Log.error("Classe: " + className + ". Acesso ao método não permitido", e);
		} catch (InvocationTargetException e) {
			Log.error("Classe: " + className + ". Contrutor da classe lançou exceção", e);
		} catch (NoSuchMethodException e) {
			Log.error("Classe: " + className + ". Construtor não encontrado", e);
		}
		return result;		
	}

	/**
	 * Método que montar o caminho da estrutura de pacote para executar a classe de negócio.
	 * @param classe Nome da classe a ser executada.
	 * @return Retorna a classe extruturada.
	 * @throws ClassNotFoundException É lançada caso a classe não seja definida.
	 */
	protected String getClassName(String classe) throws ClassNotFoundException {
		
		String packageName = this.getPackageName();
		String proccess = this.getProcesso();
		
		// Adiciona o pacote do cenário passado
		String className = packageName + (classe.substring(0,1).toLowerCase() + classe.substring(1));
		
		// Adiciona o nome do subpacote
		className += "." + (proccess.substring(0,1).toLowerCase() + proccess.substring(1)) + ".";
		
		// Adiciona o nome da classe
		className += classe;
		
		className += proccess;
		
		Class.forName(className);	
		
		return className;
	}

	/**
	 * Método que irá lançar as classes dos cenários como threads.
	 * @param className Caminho e nome da classe a ser executado.
	 * @param periodo Periodo passado como parametro para a classe.
	 * @return Retorna verdadeiro caso o processos seja executado com sucesso.
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	protected boolean launchClass(String className, int periodo) throws ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> classe = Class.forName(className);
		
		Class<?> parameterTypes[] = new Class[2];
		Object parameterValues[] = new Object[2];
		
		parameterTypes[0] = this.getClass();
		parameterTypes[1] = Integer.class;
		
		parameterValues[0] = this;
		parameterValues[1] = new Integer(periodo);
		
		Runnable target = (Runnable) classe.getConstructor(parameterTypes).newInstance(parameterValues);

		new Thread(target).start();
		
		return true;
	}
	
}