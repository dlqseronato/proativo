package com.proativo.util.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.jdbc.OracleTypes;

import com.proativo.util.QueryWarehouse;
import com.proativo.util.connection.Connections;
import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.exception.TratamentoException;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;
import com.proativo.util.vo.ScriptVo;

/**
 * Classe abstrata de padronização DAO.
 * 
 * @author G0030353 - Rhuan Krum
 *
 */

public class OraUtil {
	private static String extensao = ".sql";

	/**
	 * Método facilitador de fechamento de objetos SQL.
	 * @param w Objeto SQL a ser fechado.
	 * @see Connections
	 */
	protected void close(Wrapper... w) {
		Connections.close(w);
	}
	
	public boolean executarScriptTratamentoCenario(CenarioVo cenario, String connectionName, String arquivoSql, Object... parametros){
		
		String sql = QueryWarehouse.getQuery(arquivoSql);
		Connection conn = null;
		PreparedStatement pstmt = null;
		Boolean result = false;
		
		try{
			conn = Connections.getConn(connectionName);
			pstmt = conn.prepareStatement(sql);
			
			for(int i = 0; i < parametros.length; i++){
				pstmt.setObject(i+1, parametros[i]);
			}
			result = pstmt.execute();
		}
		catch (Exception e){
			result = false;
			Log.error("Erro ao executar o script de tratamento do Cenario", e);
		}
		finally{
			close(pstmt, conn);
		}
		return result;
	}
	
	 /**
     * @deprecated Utilizar o metodo executarScriptTratamento
     */
	
	@Deprecated
	public boolean executarScriptTratamentoCenario(CenarioVo cenario, DynamicConnection dynamicConnection, String arquivoSql, Object... parametros) {
		String sql = QueryWarehouse.getQuery(arquivoSql);
		PreparedStatement pstmt = null;
		boolean result = false;
	    
		try
	    {
	      pstmt = dynamicConnection.prepareStatement(sql);
	
	      for (int i = 0; i < parametros.length; i++) {
	        pstmt.setObject(i + 1, parametros[i]);
	      }
	      result = (pstmt.executeUpdate() != 0);
	    }
	    catch (Exception e) {
	      result = false;
	      Log.error("Erro ao executar o script de tratamento do Cenario", e);
	    }
	    finally {
	      close(pstmt);
	      dynamicConnection.setUsed(false);
	    }
	    return result;
	}
	
	public synchronized void  executarScriptTratamento(CenarioVo cenario, DynamicConnection dynamicConnection, String arquivoSql, Object... parametros)  throws TratamentoException{
		
		try{
			String sql;
		
			if( (arquivoSql.toLowerCase().startsWith("insert ")) || (arquivoSql.toLowerCase().startsWith("delete ")) || (arquivoSql.toLowerCase().startsWith("update ")) || (arquivoSql.toLowerCase().startsWith("select "))){
				sql = arquivoSql;
			} else {
				sql = QueryWarehouse.getQuery(arquivoSql);
			}
		
			if(cenario.getGerarScript() == 0){
				PreparedStatement pst = null;
				
				pst = dynamicConnection.prepareStatement(sql);
				
				for(int i = 0; i < parametros.length; i++){
					pst.setObject(i+1, parametros[i]);
				}
				pst.executeUpdate();
				
				close(pst);
			} else {
				String base = dynamicConnection.getConnectionName();
				base = base.replace("PROATIVO_", "");
				gerarScriptSQL(cenario, sql, base, parametros);
			}
		}catch(Exception e){
			throw new TratamentoException("Erro ao realizar tratamento", e);
		} finally{
			dynamicConnection.setUsed(false);
		}
	}
	
	public synchronized void executarScriptTratamentoCenario(DynamicConnection dynamicConnection, String arquivoSql, Object... parametros) throws TratamentoException{
		PreparedStatement pst = null;
		String sql = null;
		
		try{
			sql = QueryWarehouse.getQuery(arquivoSql);		
			pst = dynamicConnection.prepareStatement(sql);
			
			for(int i = 0; i < parametros.length; i++){
				pst.setObject(i+1, parametros[i]);
			}
			
			pst.executeUpdate();
			
		}catch(Exception e){
			throw new TratamentoException("Erro ao realizar tratamento", e);
		} finally{
			close(pst);
			dynamicConnection.setUsed(false);
		}
	}
	
	/**
    * @return Retorna 1 se o cliente estiver na base CT1, 2 se estiver na CT2 e -1 se não encontrar nas beases.
    */
    protected int executarProcedure(String procedure, String parametro) {
    	
    	Connection conn = null;
    	CallableStatement stmt = null;
    	ResultSet rs = null;
    	
    	try {
			conn = Connections.getConn(Connections.CONN_KENAN_CAT);
			stmt = conn.prepareCall("{ call "+procedure+"(?, ?, ?, ?) }");
			stmt.setString(1, parametro);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.registerOutParameter(3, OracleTypes.NUMBER);
			stmt.registerOutParameter(4, OracleTypes.VARCHAR);
			
			stmt.execute();
			BigDecimal errorCode = (BigDecimal)stmt.getObject(3);
			String errorMessage = stmt.getString(4);
			
			if(errorCode!=null && errorCode.intValue()!=0) {
				if(errorCode.intValue() == -12){
					Log.info("Parametro = ["+parametro+"] "+ errorMessage);
				}else{
					SQLException e = new SQLException("Parametro = ["+parametro+"] "+ errorMessage);
					Log.error(errorMessage, e);
				}
			}else{
				rs = (ResultSet)stmt.getObject(2);
				
				if(rs.next()) {
					return Integer.parseInt(rs.getString("DS_DATABASE").trim().substring(rs.getString("DS_DATABASE").trim().length()-1));
				}
			}
		} catch (SQLException e) {
			Log.error("Ocorreu um erro ao buscar qual o banco em que o usuario esta pelo Parametro ["+parametro+"]", e);
		} finally {
			close(rs, stmt, conn);
		}    	
		return -1;
	}
    
	private synchronized String formataSql(String sql, Object... parametros){
		String sqlLower = null;
		int i = 1;
		for(Object obj: parametros){
			if(!(obj instanceof DynamicConnection)){
				int index = sql.indexOf("?",i);
				StringBuilder novoSql = new StringBuilder(sql);
				novoSql.deleteCharAt(index);
				
				if(obj instanceof String){
					obj = "'" + obj + "'";
				}else if (obj instanceof Timestamp){
					obj = "to_date('"+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(obj) + "','dd/mm/yyyy hh24:mi:ss')";
				}else if( obj instanceof Date || obj instanceof java.sql.Date){
					obj = "to_date('"+ new SimpleDateFormat("dd/MM/yyyy").format(obj) + "','dd/mm/yyyy')";
				}else if (obj == null){
					obj = "null";
				}
				
				novoSql.insert(index, obj);
				sql = novoSql.toString();
				i = (obj.toString().length() + index);
			}
		}
		
		sql = sql.replaceAll(System.lineSeparator(), " ");
			
		
		/*Caso a query seja uma PL Anônima ela não teve
		 * ser acrescida do caracter ";" nem retirados os
		 * caracteres de recuo de linha, pois esse tipo
		 * de script será executado por produção e não pelo
		 * portal SAN onde se executam os updates.
		 * */
		sqlLower = sql.toLowerCase().trim(); 
		if (!(sqlLower.startsWith("begin") || sqlLower.startsWith("declare"))){
			sql = sql.replaceAll("\n", " ");
			sql += ";";
		}
		
		return sql;
	}
    
    protected synchronized void gerarArquivoContingencia(CenarioVo cenario, String descricao, String sql, Object... parametros){
		String nomeCenario = "cenario_" + cenario.getCenario() + "_" + cenario.getSegmento() + "_";
		String execucao = "_exec_"+cenario.getIdExecucao() + "_";
		String nomeArquivo = nomeCenario+descricao+execucao+extensao;
		
		File file = new File(cenario.getDiretorioContingencia().getAbsolutePath() + System.getProperty("file.separator") + nomeArquivo);
		Log.info("Gerado arquivo em: " + file.getAbsolutePath());
		sql = formataSql(sql, parametros);
		geraArquivoContingencia(sql, file);
	}
    
    public synchronized void gerarArquivo(CenarioVo cenario, List<ScriptVo> scripts){
		String nomeCenario = "cenario_" + cenario.getCenario() + "_" + cenario.getSegmento() + "_";
		List<File> arquivos = new ArrayList<File>();
		
		
		for(ScriptVo script: scripts){
			String execucao = "_exec_"+cenario.getIdExecucao() + "_" + script.getBase();
			String nomeArquivo = nomeCenario+"tratamento"+execucao+extensao;
			File file = new File(cenario.getDiretorioContingencia().getAbsolutePath() + System.getProperty("file.separator") + nomeArquivo);
			
			if(!file.exists()){
				arquivos.add(file);
			}
			
			geraArquivoContingencia(script.getComando(), file);
		}
		
		cenario.setArquivosGerados(arquivos);
		
	}
    
	protected synchronized static void geraArquivoContingencia(String registro, File arquivo){
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(arquivo, true);
			bw = new BufferedWriter(fw);
			
				bw.write(registro);
				bw.newLine();
			
		} catch (IOException e) {
			Log.error("Erro ao gerar arquivo de contingencia", e);
		} finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				Log.error("Erro ao fechar arquivo de contingencia", e);
			}
		}
	}
	
	public synchronized void gerarScriptSQL(CenarioVo cenario, String sql, String base, Object... parametros){
		sql = formataSql(sql, parametros);
		inserirScripts(cenario, sql, base);
	}
	
	public synchronized void inserirScripts(CenarioVo cenario, String script, String base){
		PreparedStatement pst = null;
		String sql = null;
		Connection conn = null;
		
		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			sql = QueryWarehouse.getQuery("proativoInserirScript");
			pst = conn.prepareStatement(sql);
			pst.setObject(1, cenario.getIdExecucao());
			pst.setObject(2, cenario.getIdCenario());
			pst.setObject(3, script);
			pst.setObject(4, base);
			pst.executeUpdate();
			
		} catch (SQLException e) {
    		Log.error("Falha ao inserir o script" , e);
		} catch (Exception e) {
			Log.error("Falha ao inserir o script" , e);
		} finally{
			close(pst, conn);
		}
	}

	public synchronized String formataLista(List<?> lista){
		String resultado = "";
		
		for(int i = 0; i<lista.size();i++){
			if(i == (lista.size()-1)){
				if(lista.get(i) instanceof Integer){
					resultado += lista.get(i);
				}else{
					resultado += "'" + lista.get(i) + "'";
				}
			} else{
				if(lista.get(i) instanceof Integer){
					resultado += lista.get(i) + ",";
				}else {
					resultado += "'" + lista.get(i) + "'" + ",";
				}
			}
		}
		
		return resultado;
	}
	
	private String[] coluna ;
	private String[] tipo ;
	private Object[] linha;
	
	public ArrayList<OraUtil> geraListaObjetos (String query, String conexao)
	{
		ArrayList<OraUtil> lista = new ArrayList<OraUtil>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		try {
			conn = Connections.getConn(conexao);
			sql = QueryWarehouse.getQuery(query);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()){
				OraUtil obj = new OraUtil();
				 obj.linha = new Object[rs.getMetaData().getColumnCount()];
				 obj.coluna = new String[rs.getMetaData().getColumnCount()];
				 obj.tipo = new String[rs.getMetaData().getColumnCount()];
					for(int i = 1; i <=rs.getMetaData().getColumnCount();  i++){

						obj.linha[i-1] = rs.getObject(i);
						obj.coluna[i-1] = rs.getMetaData().getColumnName(i);									
						obj.tipo[i-1] = rs.getMetaData().getColumnTypeName(i);
						
					}				
			lista.add(obj);
			}
			
		} catch (Exception e) {
			Log.error("Falha ao Criar Objeto generico", e);
		} finally{
			close(conn, rs, pst);
		}	
	
		return  lista;		
	}	

	
	
	public String getListaElementos(String sql, String conexao){
		ArrayList<OraUtil> listaObj =null;
		listaObj = geraListaObjetos(sql, conexao);
		//Verificanod se a lisata possui mais de 999 elementos e gerando uma exceção visto que o in não pode recerber mais de mil
		if (listaObj.size() >999){
			 throw new IllegalArgumentException();			 
		 }
		String retorno = ""; 
		String complemento = "'";//para caso fosse um varchar
		if(listaObj.get(0).tipo.equals("NUMBER")||listaObj.get(0).tipo.equals("INT"))//No caso só sera passado elemento e row_ids, logo não há necessidade de ter outro tipoalem de numerico e varchar
		complemento = "";
		for (OraUtil objetoGenerico : listaObj) {
			if(!listaObj.get(0).equals(objetoGenerico)) 
				retorno = retorno+ ", ";
			retorno = retorno +complemento+ objetoGenerico.linha[0]+complemento; 
		}		
		return retorno;
	}
	
	public String getTemporariaElementos(String sql, String conexao){
		ArrayList<OraUtil> listaObj =null;
		listaObj = geraListaObjetos(sql, conexao);
		String retorno = ""; 
		String complemento = "'";//para caso fosse um varchar
		for (OraUtil objetoGenerico : listaObj) {
			if(!listaObj.get(0).equals(objetoGenerico)) 
				retorno = retorno+ " Union ";
			retorno = retorno + " Select ";
			for(int i =0;i<objetoGenerico.coluna.length;i++){
				if(i>0)
					retorno = retorno + ",";
				if(objetoGenerico.tipo[i].equals("NUMBER")||objetoGenerico.tipo[i].equals("INT"))//No caso só sera passado elemento e row_ids, logo não há necessidade de ter outro tipoalem de numerico e varchar
					complemento = "";
				if(objetoGenerico.tipo[i].substring(0,3).equals("TIME")||objetoGenerico.tipo[i].equals("DATE"))
				{
					SimpleDateFormat toDate = new SimpleDateFormat("yyyyMMdd");
					retorno = retorno + " TO_DATE('"+toDate.format(objetoGenerico.linha[i])+"','YYYYMMDD') " + objetoGenerico.coluna[i];
					
				}else
					retorno = retorno  +" " +complemento+ objetoGenerico.linha[i]+complemento +" " + objetoGenerico.coluna[i]; 
				complemento = "'";
			}
			retorno = retorno + " From dual ";
		}
	
		return retorno;
	}
}
