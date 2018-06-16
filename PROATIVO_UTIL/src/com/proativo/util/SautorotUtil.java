package com.proativo.util;

import java.sql.Timestamp;

import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.dao.OraUtilKenan;
import com.proativo.util.exception.TratamentoException;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;

/**
 * Classe criada 
 * 
 * @author G0030353 - Rhuan Krum
 * @since 24/10/2016
 * 
 */

public class SautorotUtil {
	
	private static Integer tempoMinutosEspera = 2;
	private static OraUtilKenan kenan;
	
	public synchronized static void gerarContingenciaDiscComponent(CenarioVo cenario, String servInst, Integer packageId, Long componentInstId,
			Integer componentInstIdServ, Timestamp endDate, Integer extIdTypeInst, String rotina, DynamicConnection dc){
			
		try {
			kenan.executarScriptTratamento(cenario, dc, "kenanContingenciaDiscComponent", 
					servInst,
					packageId,
					componentInstId,
					componentInstIdServ,
					endDate,
					extIdTypeInst,
					rotina
					);
			
		
		} catch (TratamentoException e) {
			Log.error("não foi possivel gerar o arquivo", e);
		}
	}
	
	
	public synchronized static void aguardarSautorotProvCharges(String rotina){
		try{
			kenan = new OraUtilKenan();
			
			Log.info("Iniciando espera de processamento do Sautorot Prov Charges");
			while (kenan.existeRegistroPendenteSautorotProvCharges(rotina)){
				Thread.sleep(tempoMinutosEspera * 1000 * 60);
			}
			Log.info("Finalizando espera de processamento do Sautorot Prov Charges");
		} catch (Exception e){
			Log.error("Erro!", e);
		}
	}
	
	public synchronized static void aguardarSautorotDiscComponent(String rotina){
		try{
			kenan = new OraUtilKenan();
			
			Log.info("Iniciando espera de processamento do Sautorot Disc Component");
			while (kenan.existeRegistroPendenteSautorotDiscComponent(rotina)){
				Thread.sleep(tempoMinutosEspera * 1000 * 60);
			}
			Log.info("Finalizando espera de processamento do Sautorot Disc Component");
		} catch (Exception e){
			Log.error("Erro!", e);
		}
	}
	
	public synchronized static void aguardarSautorotCeaseAccount(String rotina){
		try{
			kenan = new OraUtilKenan();
			
			Log.info("Iniciando espera de processamento do Sautorot Cease Account");
			while (kenan.existeRegistroPendenteSautorotCeaseAccount(rotina)){
				Thread.sleep(tempoMinutosEspera * 1000 * 60);
			}
			Log.info("Finalizando espera de processamento do Sautorot Cease Account");
		} catch (Exception e){
			Log.error("Erro!", e);
		}
	}
}
