package com.proativo.util;

import java.util.List;

import com.proativo.util.dao.OraUtilProativo;
import com.proativo.util.log.Log;
import com.proativo.util.report.ReportUtil;
import com.proativo.util.vo.CenarioVo;

public class AlarmeUtil {
	private static OraUtilProativo proativo = new OraUtilProativo();
	
	public static void gerarAlarme(CenarioVo cenario){
		String sql = proativo.buscarQueryAlarme(cenario);
		
		Object[] cabecalho = {"CENARIO", "SEGMENTO", "DESCRICAO", "CONTA", "CICLO", "KENAN_DB_ID"};
		
		List<Object[]> linhas = proativo.buscarContasAlarme(cenario, sql);
		
		String relatorio = ReportUtil.geraRelatorioAlarme(cenario, "Alarme", cabecalho, linhas, true);
		
		if(relatorio != null){
			Log.info("Alarme gerado e e enviado com sucesso: " + relatorio);
		}else{
			Log.info("Sem casos levantados");
		}
	}

}
