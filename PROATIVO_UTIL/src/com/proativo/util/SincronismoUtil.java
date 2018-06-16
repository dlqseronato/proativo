package com.proativo.util;

import com.proativo.util.dao.OraUtilProativo;
import com.proativo.util.log.Log;

public class SincronismoUtil {
	
	private static Integer tempoMinutosEspera = 10;
	private static OraUtilProativo proativo;
	
	public synchronized static void aguardarSincronismo(String rotina){
		try{
			proativo = new OraUtilProativo();
			
			Log.info("Iniciando espera de processamento de sincronismo");
			
			while (proativo.existeRegistroPendenteSincronismo(rotina)){
				
				Thread.sleep(tempoMinutosEspera * 1000 * 60);
				
			}
			
			
			Log.info("Finalizando espera de processamento de sincronismo");
		} catch (Exception e){
			Log.error("Erro!", e);
		}
	}

}
