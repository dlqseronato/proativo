package com.proativo.util.thread;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.proativo.util.connection.Connections;
import com.proativo.util.connection.DBPoolConfigFileFormatException;
import com.proativo.util.connection.DBPoolConfigFileManager;
import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.log.Log;

/**
 * Classe criada utilizando a estrutura da ThreadManager, para gerenciar threads juntamente com 
 * conexoes de base, para que seja possivel reaproveitar uma mesma conexao em varias consultas de base,
 * sem a necessidade de fechar e abrir uma nova conexao - o ganho de desempenho depende da quantidade de
 * dados/consultas a serem executadas pela aplicacao.
 * 
 * @author G0030353 - Rhuan Pablo Ribeiro Krum
 * @since 04/07/2013
 */
public class ThreadManagerDynamicConnection implements Observer {
	
	protected int NUM_THREADS = 0;
	protected int numThreadsFinished = 0;

	/*
	 * Quantidade de Threads configuradas no arquivo threads.properties
	 */
	private int numThreadsConfig;
	
	/*
	 * Quantidade de conexoes configuradas no arquivo dbpool
	 */
	private Integer numConexoesConfig;
	
	private Map<String, List<DynamicConnection>> dynamicConnections = new HashMap<String, List<DynamicConnection>>();
	private DynamicConnection dynamicConnection = null;
	private DBPoolConfigFileManager dbpool = new DBPoolConfigFileManager();

	public ThreadManagerDynamicConnection(){}	
		
	/**
	 * 
	 * Método para realizar a chamada de threads utilizando uma lista de objetos, 
	 * gerenciando dinamicamente as conexoes utilizadas sem fecha-las, afim de obter
	 * melhor desempenho em caso de listas extensas de dados.
	 * 
	 * @author G0030353 - Rhuan Krum - 13/06/2013
	 * 
	 * @param dados
	 */
	public synchronized void executar(List<?> dados, ActionAbstract<?> validateObject, Integer qtdeThreads, String...connectionNames){
		try {
			if(qtdeThreads == 0 || qtdeThreads == null){
				numThreadsConfig = 1;
			}else{
				numThreadsConfig = qtdeThreads;
			}
				
			numConexoesConfig = null;
			
			if(numThreadsConfig > dados.size()) numThreadsConfig = dados.size();
			if(numThreadsConfig <= 0) numThreadsConfig = 1;
			
			this.getDynamicConnections(connectionNames);

			Log.info("Paralelisando "
					+ validateObject.getClass().getName()
					+ " em "
					+ numThreadsConfig
					+ " thread(s) para " + NumberFormat.getInstance().format(dados.size())  + " item(ns)");

			numThreadsFinished = 0;
			NUM_THREADS = 0;
			// *************************************************************
			// Threads que consultam os dados no Siebel
			for (int i = 0; i < numThreadsConfig; i++){
								
				int begin = i * (dados.size() / numThreadsConfig);
				int end = (i + 1) * (dados.size() / numThreadsConfig);

				// Se for a última thread, usa o valor total da colecao
				if (i == (numThreadsConfig - 1))
					end = dados.size();

				List<?> list = dados.subList(begin, end);

				WorkerThread thread = new WorkerThread(this, list, validateObject, "Thread[" + (begin + 1) + "-" + end + "]");

				new Thread(thread).start();
				NUM_THREADS++;
			}
			this.wait();
			
			Log.info("Fim paralelismo "+ validateObject.getClass().getName() + ". Executadas " + NUM_THREADS + " threads");
			
			for(String connectionName : dynamicConnections.keySet()){
				closeConnections(connectionName);
			}
			dynamicConnections.clear();
		}
		catch (InterruptedException e){
			Log.error("Ocorreu um erro inesperado!", e);
			System.exit(-1);
		}
	}
	
	
	public synchronized void executar(List<?> dados, ActionAbstract<?> validateObject, Integer qttPerThread, Integer threadWait, String...connectionNames){
		try {
			if(qttPerThread == 0 || qttPerThread == null){
				numThreadsConfig = 1;
			}else{
				numThreadsConfig = Math.abs(dados.size()/qttPerThread);
			}
				
			numConexoesConfig = null;
			
			if(numThreadsConfig > dados.size()) numThreadsConfig = dados.size();
			if(numThreadsConfig <= 0) numThreadsConfig = 1;
			
			this.getDynamicConnections(connectionNames);

			Log.info("Paralelisando "
					+ validateObject.getClass().getName()
					+ " em "
					+ numThreadsConfig
					+ " thread(s) para " + NumberFormat.getInstance().format(dados.size())  + " item(ns)");

			numThreadsFinished = 0;
			NUM_THREADS = 0;
			// *************************************************************
			// Threads que consultam os dados no Siebel
			for (int i = 0; i < numThreadsConfig; i++){
								
				int begin = i * (dados.size() / numThreadsConfig);
				int end = (i + 1) * (dados.size() / numThreadsConfig);

				// Se for a última thread, usa o valor total da colecao
				if (i == (numThreadsConfig - 1))
					end = dados.size();

				List<?> list = dados.subList(begin, end);

				WorkerThread thread = new WorkerThread(this, list, validateObject, "Thread[" + (begin + 1) + "-" + end + "]");
				Log.info( "Thread[" + (begin + 1) + "-" + end + "] Dormindo "+threadWait+" ms.");
				Thread.sleep(threadWait);
				new Thread(thread).start();
				NUM_THREADS++;
			}
			this.wait();
			
			Log.info("Fim paralelismo "+ validateObject.getClass().getName() + ". Executadas " + NUM_THREADS + " threads");
			
			for(String connectionName : dynamicConnections.keySet()){
				closeConnections(connectionName);
			}
			dynamicConnections.clear();
		}
		catch (InterruptedException e){
			Log.error("Ocorreu um erro inesperado!", e);
			System.exit(-1);
		}
	}
	
	/**
	 * 
	 * Método para realizar a abertura das conexoes solicitadas utilizando um array de Connections-name
	 * da classe gvt.util.Connections.
	 * A quantidade de conexoes abertas varia dependendo da quantidade de sessoes limitadas nos arquivos
	 * .dbpool, como também o número de threads, pois o número de conexoes a serem abertas deve ser menor
	 * do que a quantidade de threads configurada.
	 * 
	 * @author G0030353 - Rhuan Krum
	 * @since 13/06/2013
	 * 
	 * @param connectionNames
	 */
	private void getDynamicConnections(String...connectionNames) {
		try{
			for(String connectionName : connectionNames){
				// Abre as conexoes a serem utilizadas
				numConexoesConfig = (dbpool.getAvailableConnections(connectionName));
								
				// Limita a quantidade de conexoes utilizadas conforme a quantidade de threads lancadas
				if(numConexoesConfig != null && numConexoesConfig > numThreadsConfig){
					numConexoesConfig = numThreadsConfig;
				}
				if(numConexoesConfig < 1){
					numConexoesConfig = 1;
				}
				// Realiza a abertura das conexoes
				for(int i = 0; i < numConexoesConfig; i++){
					dynamicConnection = new DynamicConnection(connectionName);
					if(dynamicConnections.containsKey(connectionName)){
						dynamicConnections.get(connectionName).add(dynamicConnection);
					}
					else{
						List<DynamicConnection> dynamicConnectionList = new ArrayList<DynamicConnection>();
						dynamicConnectionList.add(dynamicConnection);
						dynamicConnections.put(connectionName, dynamicConnectionList);
					}
				}
			}
		} catch (SQLException e) {
			Log.error("Ocorreu uma falha ao realizar a abertura de uma conexao.", e);
		} catch (DBPoolConfigFileFormatException e) {
			Log.error("Ocorreu uma falha ao realizar a leitura de um arquivo .dbpool.", e);
		} catch(Exception e){
			Log.error("Ocorreu uma falha ao realizar a abertura de conexoes.", e);
		}
	}
	
	/**
	 * Método utilizado para realizar o fechamento de todas as conexoes abertas
	 *
	 * @author G0024266 - Andre Luiz Przynyczuk
	 * @since 01/05/2015
	 */
	private void closeConnections(String connectionName){
		for(DynamicConnection dc : dynamicConnections.get(connectionName)){
			Connections.close(dc.getConn());
		}
	}
	
	/**
	 * Método utilizado para realizar a captura de uma conexao disponivel utilizando o nome de uma conexao especifica,
	 * a partir da classe Connections.
	 * Caso nao exista nenhuma conexao disponivel no momento, a thread que chamou este método
	 * aguarda por 200 milissegundos para entao realizar uma nova tentativa.
	 * Sao realizadas no total 1500 tentativas, totalizando 5 minutos.
	 * 
	 * @author G0030353 - Rhuan Krum
	 * @since 13/06/2013
	 * 
	 * @param String connectionName - Connections.CONN...
	 */
	public DynamicConnection getAvailableConnection(String connectionName){
		try {
			boolean isConnectionAvailable = false;
			int tries = 0;
			
			// Verifica se conexao solicitada esta entre as conexoes existentes
			if(dynamicConnections.containsKey(connectionName)){
				
				// Realiza tentativa de utilizar uma das conexoes possiveis
				while(!isConnectionAvailable && ++tries < 150000){
					for(DynamicConnection dynamicConnection : dynamicConnections.get(connectionName)){
						if(!dynamicConnection.isUsed() && dynamicConnection.isUsable()){
							isConnectionAvailable = true;
							return dynamicConnection;
						}
					}
					if(!isConnectionAvailable) Thread.sleep(10);
				}
			}
			// Se nenhuma conexao estiver disponivel, uma excecao sera lancada
			else if(!isConnectionAvailable){
				Log.error("Ocorreu uma falha ao capturar uma conexao disponivel para a base '" + connectionName + "'. Nao ha nenhuma conexao disponivel para uso. A aplicacao sera encerrada!", null);
				System.exit(-1);
			}
			// Valida se a conexao existe na lista de conexoes disponiveis
			else{
				Log.error("A conexao para a base '" + connectionName + "' nao existe na lista de conexoes disponiveis. A aplicacao sera encerrada!", null);
				System.exit(-1);
			}
		} catch (InterruptedException e) {
			Log.error("Ocorreu uma falha ao capturar uma conexao disponivel", e);
		} catch (Exception e){
			Log.error("Ocorreu uma falha ao capturar uma conexao disponivel", e);
		}
		
		return null;
	}
	
	/**
	 * Método utilizado para realizar a captura de uma conexao disponivel utilizando o identificador kenanDbId.
	 * Caso nao exista nenhuma conexao disponivel no momento, a thread que chamou este método
	 * aguarda por 200 milissegundos para entao realizar uma nova tentativa.
	 * Sao realizadas no total 1500 tentativas, totalizando 5 minutos.
	 * Os DBPOOLS acionaveis por este método sao os relacionados aos atributos CONN_ARBOR, CONN_ARBOR_PROD e CONN_ARBOR_READ
	 * 
	 * @author G0030353 - Rhuan Krum
	 * @since 13/06/2013
	 * 
	 * @param Integer kenanDbId
	 */
	public DynamicConnection getAvailableConnection(Integer kenanDbId){
		try {
			boolean isConnectionAvailable = false;
			int tries = 0;
			String connectionName = Connections.CONN_KENAN_CT+kenanDbId;
			
			// Verifica se conexao solicitada esta entre as conexoes existentes
			if(dynamicConnections.containsKey(connectionName)){
				// Realiza tentativa de utilizar uma das conexoes possiveis
				while(!isConnectionAvailable && ++tries < 15000){
					for(DynamicConnection dynamicConnection : dynamicConnections.get(connectionName)){
						if(!dynamicConnection.isUsed() && dynamicConnection.isUsable()){
							isConnectionAvailable = true;
							return dynamicConnection;
						}
					}
					if(!isConnectionAvailable) Thread.sleep(10);
				}
			}
			// Se nenhuma conexao estiver disponivel, uma excecao sera lancada
			else if(!isConnectionAvailable){
				Log.error("Ocorreu uma falha ao capturar uma conexao disponivel para a base PBCT'" + kenanDbId + "'. Nao ha nenhuma conexao disponivel para uso. A aplicacao sera encerrada!", null);
			}
			// Valida se a conexao existe na lista de conexoes disponiveis
			else{
				Log.error("A conexao para a base PBCT'" + kenanDbId + "' nao existe na lista de conexoes disponiveis. A aplicacao sera encerrada!", null);
			}
		} catch (InterruptedException e) {
			Log.error("Ocorreu uma falha ao capturar uma conexao disponivel.", e);
		} catch (Exception e){
			Log.error("Ocorreu uma falha ao capturar uma conexao disponivel.", e);
		}
		return null;
	}

	public synchronized void update(Observable o, Object arg) {
		numThreadsFinished++;
		if (numThreadsFinished == NUM_THREADS){
			this.notifyAll();
		}
	}
}
