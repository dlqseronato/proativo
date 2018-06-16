package com.proativo.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import com.proativo.util.connection.Connections;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;
import com.proativo.util.vo.ProcessamentoCicloVo;
import com.proativo.util.vo.ProcessoVo;

/**
 * 
 * Classe com m�todos gen�ricos para fazer insert/update em uma tabela de log para
 * registrar o in�cio e/ou fim de uma determinada aplica��o/processo.
 * 
 * Defini��o b�sica da tabela utilizada:
 * 
 * CREATE TABLE bq_processos(
 *  aplicacao VARCHAR2(100),
 *  processo VARCHAR2(250),
 *  inicio DATE,
 *  fim DATE);
 * 
 * Quando o processo n�o existe na tabela de log, � inserido um novo registro. Caso o processo
 * ja extista, ele faz um update, marcando o fim com null.
 * Na finaliza��o do processo, � verificado se existe o registro e ent�o feito o update.
 * 
 * A classe n�o retorna erro na inexistencia da tabela.
 * A conex�o � passada para cada m�todo para n�o ter a necessidade de deixar uma conex�o aberta.
 * 
 * @author G0002422
 * @author Andre Rosot - Reestrutura��o para englobar funcionalidades do projeto BSS_UTIL, na tentaiva de unificar o BQ_UTIL como unico projeto auxiliar para todos os DQs
 *
 */
public abstract class Processo extends Observable{
	
	protected String[] argumentos;
	
	public void setArgumentos(String argumentos) {
		this.argumentos = argumentos.split(",");
	}
	
	/**
	 * M�todo de execu��o dos processos.
	 * @param processos Lista de processos a serem executados.
	 */
	public abstract void executar(CenarioVo cenario, ProcessamentoCicloVo ciclo);
	
	/**
	 * M�todo que faz o insert/update marcando o in�cio do processo na tabela de log.
	 * 
	 * @param conn - Connection
	 * @param tabela - String
	 * @param aplicacao - String
	 * @param processo - String
	 */
	@Deprecated
	public static void start(Connection conn, String tabela, String aplicacao, String processo){
		
		String strSql = "";
		
		if(processoExistente(conn, tabela, aplicacao, processo)){
			strSql = "update " + tabela + " set inicio = sysdate, fim = null where aplicacao = '" + aplicacao + "' and processo = '" + processo + "'";
		}else{
			strSql = "insert into " + tabela + " (aplicacao, processo, inicio) values ('" + aplicacao + "', '" + processo + "', sysdate)";
		}
		
		Statement stmt;
		try {
			stmt = conn.createStatement();
		
			stmt.executeUpdate(strSql);
			
			conn.commit();
			
		} catch (SQLException e) {
			Log.error("N�o foi poss�vel atualizar a tabela de Log.", e);
		}
		
	}
	
	public static void start(String tabela, String aplicacao, String processo) {
		String strSql = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			
			strSql = (processoExistente(conn, tabela, aplicacao, processo)?"update " + tabela + " set inicio = sysdate, fim = null where aplicacao = ? and processo = ?":"insert into " + tabela + " (aplicacao, processo, inicio) values (?, ?, sysdate)");
			ps = conn.prepareStatement(strSql);
			ps.setString(1, aplicacao);
			ps.setString(2, processo);
			ps.executeUpdate();			
		} catch (SQLException e) {
			Log.error("N�o foi poss�vel atualizar a tabela de Log.", e);
		} finally {
			Connections.close(ps, conn);
		}
	}
	
	/**
	 * M�todo que faz o update marcando o fim do processo na tabela de log.
	 * 
	 * @param conn - Connection
	 * @param tabela - String
	 * @param aplicacao - String
	 * @param processo - String
	 */
	@Deprecated
	public static void finish(Connection conn, String tabela, String aplicacao, String processo){
		
		
		String strSql = "update " + tabela + " set fim = sysdate where aplicacao = '" + aplicacao + "' and processo = '" + processo + "'";
		
		Statement stmt;
		try {
			stmt = conn.createStatement();
			
			if(processoExistente(conn, tabela, aplicacao, processo)){
				stmt.executeUpdate(strSql);
				conn.commit();
			}
			
		} catch (SQLException e) {
			Log.error("N�o foi poss�vel atualizar a tabela de Log.", e);
		}		
	}
	
	public static void finish(String tabela, String aplicacao, String processo) {
		final String strSql = "update " + tabela + " set fim = sysdate where aplicacao = ? and processo = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			
			ps = conn.prepareStatement(strSql);
			ps.setString(1, aplicacao);
			ps.setString(2, processo);
			ps.executeUpdate();			
		} catch (SQLException e) {
			Log.error("N�o foi poss�vel atualizar a tabela de Log.", e);
		} finally {
			Connections.close(ps);
			Connections.close(conn);
		}
	}
	
	private static boolean processoExistente(Connection conn, String tabela, String aplicacao, String processo){
		
		boolean exist = false;
		
		String strSql = "select 1 from " + tabela + " where aplicacao = '" + aplicacao + "' and processo = '" + processo + "'";
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strSql);
			
			if(rs.next()){
				exist = true;
			}			
		} catch (SQLException e) {
			Log.error("N�o foi poss�vel verificar a tabela de Log.", e);
		} finally {
			Connections.close(stmt);
		}
		return exist;		
	}

	public static List<ProcessoVo> buscarProcessos(final String aplicacao, String executionGroup) {
		List<ProcessoVo> processos = new LinkedList<ProcessoVo>();
		
		
		
		ProcessoVo processo = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		final String sql = "SELECT ID, processo, PERIODO FROM bss_processos WHERE EXECUTAVEL = 1 AND aplicacao = ? AND (GRUPO = ? OR ? IS NULL) ORDER BY ID";
		
		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			ps = conn.prepareStatement(sql);
			ps.setString(1, aplicacao);
			ps.setString(2, executionGroup);
			ps.setString(3, executionGroup);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				processo = new ProcessoVo(aplicacao);
				processo.setId(rs.getInt("ID"));
				processo.setClasse(rs.getString("PROCESSO"));
				processo.setPeriodo(rs.getInt("PERIODO"));
				processos.add(processo);
			}
		} catch (SQLException e) {
			Log.error("Erro ao buscar os processos da aplica��o "+ aplicacao, e);
		} finally {
			Connections.close(rs);
			Connections.close(ps);
			Connections.close(conn);
		}
		return processos;
	}
}