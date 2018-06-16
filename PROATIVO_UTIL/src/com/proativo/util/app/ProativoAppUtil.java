package com.proativo.util.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.proativo.util.AlarmeUtil;
import com.proativo.util.Processo;
import com.proativo.util.dao.OraUtilKenan;
import com.proativo.util.dao.OraUtilProativo;
import com.proativo.util.enums.Periodicidade;
import com.proativo.util.enums.TipoExecucao;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;
import com.proativo.util.vo.ProcessamentoCicloVo;
import com.proativo.util.vo.ScriptVo;

/**
 *Classe criada para padronizar a execucao dos cenarios.
 * 
 * @author G0030353 - Rhuan Pablo Ribeiro Krum
 * @since 28/09/2015
 * 
 * @alterado G0024266 - Andre Luiz Przynyczuk
 * @since 08/10/2015
 * 
 */

public class ProativoAppUtil {
	
	public static void executaAplicacao(String numeroCenario, String segmento, String[] args, Class<?> classeMain){
		try{
			//identifica a thread principal da aplicacao
			Thread.currentThread().setName("cenario_" + numeroCenario);
			
			long tInicio = System.currentTimeMillis();
			long tFinal;
			long tTotal;
			
			CenarioVo cenario = new CenarioVo(numeroCenario);
			ProcessamentoCicloVo ciclo = null;
			OraUtilProativo proativo = new OraUtilProativo();
			OraUtilKenan kenan = new OraUtilKenan();
			
			if(ProativoAppUtil.validaSegmento(segmento)){
				cenario.setSegmento(segmento);
			}else{
				throw new RuntimeException("Formato do segmento nao e valido! Favor informar o segmento corretamente (Ex. C ou R)");
			}
			
			// Buscar processos para executar
			proativo.buscarControleExecucao(cenario);
			
			if(!cenario.isExecutavel()){
				Log.info("Cenario configurado para nao executar.");
				System.exit(0);
			}
			
			cenario.setSegmentosKenan(proativo.buscarSegmentosKenan(cenario));
				
			// Inicializar Aplicacao
			configuraAplicacao(cenario);
			
			
			proativo.inserirLogExecucaoCenario(cenario);
			Log.info("Iniciando Cenario " + cenario.getCenario() + " - Execucao " + cenario.getIdExecucao());
			
			// Buscar ciclos conforme parametro de processamento informado
		   if(cenario.getPeriodicidade() == Periodicidade.CICLO){
				ciclo = kenan.buscarCicloProcessamentoAtual();
			}
			// Trabalhar com todos os ciclos
			else{
				ciclo = new ProcessamentoCicloVo();
			}
			
			if(args != null && args.length>0 ){
				ciclo.setParametros(args);
			}
		   
			kenan.buscarCicloPorProcessamento(ciclo);
			
			cenario.setProcessamentoCiclo(ciclo.getProcessamentoCiclo());
			
			// Executar processos
			if(cenario.getTipoExecucao() == TipoExecucao.LEVANTAMENTO){
				Log.info("Iniciando processo Load para " + getCiclosExecucao(cenario, ciclo));
				((Processo) Class.forName(getPackageName(classeMain) + "load.Load").newInstance()).executar(cenario, ciclo);
			}
			else if(cenario.getTipoExecucao() == TipoExecucao.LEVANTAMENTO_E_TRATAMENTO){
				
				try{
					if(Class.forName(getPackageName(classeMain) + "load.Load") != null){
						Log.info("Iniciando processo Load para " + getCiclosExecucao(cenario, ciclo));
						((Processo) Class.forName(getPackageName(classeMain) + "load.Load").newInstance()).executar(cenario, ciclo);
					}
				} catch(NoClassDefFoundError e){
					Log.info("Cenario " + cenario.getCenario() + " nao possui subprocesso Load");
				} catch(Exception e){
					Log.info("Cenario " + cenario.getCenario() + " esta com erro no processo Load");
				}
				
				try{
					if(Class.forName(getPackageName(classeMain) + "action.Action") != null){
						Log.info("Iniciando processo Action para execucao " + cenario.getIdExecucao());
						((Processo) Class.forName(getPackageName(classeMain) + "action.Action").newInstance()).executar(cenario, ciclo);
					}
				} catch(Exception e){
					Log.info("Cenario " + cenario.getCenario() + " nao possui subprocesso Action");
				}
				
				try{
					if(Class.forName(getPackageName(classeMain) + "validation.Validation") != null){
						Log.info("Iniciando processo Validation para execucao " + cenario.getIdExecucao());
						((Processo) Class.forName(getPackageName(classeMain) + "validation.Validation").newInstance()).executar(cenario, ciclo);
					}
				} catch(Exception e){
					Log.info("Cenario " + cenario.getCenario() + " nao possui subprocesso Validation");
				}
			}
			else if(cenario.getTipoExecucao() == TipoExecucao.ALARME){
				
				try{
					if(Class.forName(getPackageName(classeMain) + "load.Load") != null){
						Log.info("Iniciando processo Load para " + getCiclosExecucao(cenario, ciclo));
						((Processo) Class.forName(getPackageName(classeMain) + "load.Load").newInstance()).executar(cenario, ciclo);
						AlarmeUtil.gerarAlarme(cenario);
					}
				} catch(Exception e){
					Log.info("Cenario " + cenario.getCenario() + " nao possui subprocesso Load");
				}
			}
			
			//Executa o relatorio
			try{
				if(Class.forName(getPackageName(classeMain) + "report.Report") != null){
					if(cenario.getRelatorio() == 1){
						Log.info("Iniciando processo Report para " + getCiclosExecucao(cenario, ciclo));
						((Processo) Class.forName(getPackageName(classeMain) + "report.Report").newInstance()).executar(cenario, ciclo);
					} else {
						Log.info("Cenario " + cenario.getCenario() + " possui subprocesso Report, porem nao esta configurado para enviar o email.");
					}
				}
			} catch(Exception e){
				Log.info("Cenario " + cenario.getCenario() + " nao possui subprocesso Report");
			}
			
			
			if(cenario.getGerarScript() == 1){
				List<ScriptVo> scripts = proativo.buscarScripts(cenario);
				
				if(scripts.size() > 0){
					proativo.gerarArquivo(cenario, scripts);
					
					for(File f: cenario.getArquivosGerados()){
						Log.info("Gerado arquivo: " + f.getAbsoluteFile());
					}
					
					//proativo.limparScripts(cenario);
					
				}
			}
			
			proativo.atualizarLogExecucaoCenario(cenario);
			
	        tFinal = System.currentTimeMillis();
			tTotal = (tFinal - tInicio) / 1000;

			Log.info("Finalizando cenario " + cenario.getCenario() + " - Execucao " + cenario.getIdExecucao() + " - Tempo de execucao: " + calculaTempoTotal(tTotal));
	        System.exit(0);
		} catch (ClassNotFoundException e){
			Log.error("Falha ao executar cenario " + numeroCenario, e);
			System.exit(-1);
		} catch (FileNotFoundException e) {
			//caso o arquivo de sql util nao esteja configurado
			Log.error("Falha ao executar cenario " + numeroCenario + ". Configuracao de arquivos SQL incorreta", e);
			System.exit(-1);
		} catch(Exception e){
			Log.error("Falha ao executar cenario " + numeroCenario, e);
		} 
	}

	public static void configuraAplicacao(CenarioVo cenario) {
		
		Properties buildProp = new Properties();

		try {
			buildProp.load(new FileInputStream("config/build.properties"));
		} catch (IOException e) {
			Log.error("Nao foi possivel ler o arquivo build.properties", e);
			System.exit(-1);
		}
		Log.info("IT Suporte Ciclo da Receita");
		Log.info(buildProp.getProperty("build.title") + " Versao " + buildProp.getProperty("build.version"));
	}
	
	public static String calculaTempoTotal(long tTotal){
		long segundos = tTotal;
		long segundo = segundos % 60; 
		long minutos = segundos / 60; 
		long minuto = minutos % 60; 
		long hora = minutos / 60; 
		String hms = String.format ("%02d:%02d:%02d", hora, minuto, segundo); 
		return hms;
	}

/*	public static boolean validaFormatoProcessamentoCiclo(String processamentoCiclo) {
		// Formato P99
		String pattern = "^P[0-9]{2}$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(processamentoCiclo);
		
		return m.find();
	}*/
	
	public static boolean validaSegmento(String segmento) {
		String pattern = "^[R|C]$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(segmento);
		
		return m.find();
	}
	
	public static String getPackageName(Class<?> classe) {
		String str = classe.getName();
		String [] vet =  str.split("\\.");
		return vet[0]+"."+vet[1]+"."+vet[2]+".bo.";
	}
	
	public static String getCiclosExecucao(CenarioVo cenario, ProcessamentoCicloVo ciclo){
		String ciclosExecucao = "";
		
		if(cenario.getPeriodicidade() == Periodicidade.DIARIA){
			ciclosExecucao = "todos os vencimentos";
		}
		else{
			ciclosExecucao += "o processamento " + ciclo.getProcessamentoCiclo();
			ciclosExecucao += (ciclo.getCiclos().size() > 1 ? " com os vencimentos " : " com o vencimento ");
			for(int i = 0; i<ciclo.getCiclos().size();i++){
				ciclosExecucao += ciclo.getCiclos().get(i) + ", ";
			} 
			ciclosExecucao = ciclosExecucao.substring(0, ciclosExecucao.length()-2);
		}
		return ciclosExecucao;
	}
}
