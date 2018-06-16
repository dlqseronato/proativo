package com.proativo.util.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proativo.util.QueryWarehouse;
import com.proativo.util.connection.Connections;
import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.enums.Periodicidade;
import com.proativo.util.enums.TipoExecucao;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;
import com.proativo.util.vo.ScriptVo;

public class OraUtilProativo extends OraUtil {

	public boolean existeRegistroPendenteSincronismo(String rotina) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean resultado = false;
		int qtdFaltaSincronizar = 0;

		try {
			String sql = QueryWarehouse.getQuery("proativoAguardarSincronismo");

			conn = Connections.getConn(Connections.CONN_PROATIVO);

			pst = conn.prepareStatement(sql);
			pst.setString(1, rotina);

			rs = pst.executeQuery();

			// Utiliza-se if pois mais de uma vlidação é realizada
			if (rs.next()) {

				qtdFaltaSincronizar = rs.getInt("quantidade");

			}
			if (qtdFaltaSincronizar >= 1) {
				Log.info("Faltam " + qtdFaltaSincronizar + " para finalizar sincronismo.");
				resultado = true;
			} else {
				resultado = false;
			}

			

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta na tabela de carga do sincronismo", e);
		}finally {
			close(rs, pst, conn);
		}

		return resultado;
	}

	public boolean validaContaSincronismo(String conta, String rotina, DynamicConnection dc) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("proativoValidaContaSincronismo");

			pst = dc.prepareStatement(sql);
			pst.setString(1, rotina);
			pst.setString(2, conta);

			rs = pst.executeQuery();
			
			if(rs.next()){
				
				resultado = true;
			}

			

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta na tabela de sincronismo", e);
		}finally {
			close(rs, pst);
			dc.setUsed(false);
		}

		return resultado;
	}

	public void inserirLogExecucaoCenario(CenarioVo cenario) {
		Connection conn = null;
		CallableStatement cstmt = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("proativoInserirLogExecucaoCenario");
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			cstmt = conn.prepareCall(sql);

			cstmt.setInt(1, cenario.getIdCenario());

			cstmt.registerOutParameter(2, Types.INTEGER);

			cstmt.execute();

			cenario.setIdExecucao(cstmt.getInt(2));
		} catch (Exception e) {
			Log.error("Erro ao inserir log de execucao do cenario: " + cenario.getCenario(), e);
			inserirLogExecucaoCenarioContingencia(cenario, sql, cenario.getIdCenario());
		} finally {
			close(cstmt, conn);
		}
	}

	private synchronized void inserirLogExecucaoCenarioContingencia(CenarioVo cenario, String sql,
			Object... parametros) {
		gerarArquivoContingencia(cenario, "inserirLogExecucao", sql, parametros);
	}

	public void atualizarLogExecucaoCenario(CenarioVo cenario) {
		Connection conn = null;
		PreparedStatement pst = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("proativoAtualizarLogExecucaoCenario");
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cenario.getIdCenario());
			pst.executeUpdate();
		} catch (Exception e) {
			Log.error("Erro ao atualizar log de execucao do cenario " + cenario.getCenario(), e);
			atualizarLogExecucaoCenarioContingencia(cenario, sql, cenario.getCenario());
		} finally {
			close(pst, conn);
		}
	}

	private synchronized void atualizarLogExecucaoCenarioContingencia(CenarioVo cenario, String sql,
			Object... parametros) {
		gerarArquivoContingencia(cenario, "atualizarLogExecucao", sql, parametros);
	}

	public void inserirCasosTratamentoCenario(CenarioVo cenario, String arquivoSql, DynamicConnection dynamicConnection,
			Object... parametros) {
		PreparedStatement pst = null;
		String sql = QueryWarehouse.getQuery(arquivoSql);

		try {
			// Função para trazer dado da conta a ser tratada. Caso não exista
			// este campo, o valor de string vazia "" é trazido
			String conta = sql.indexOf("conta") > 0 ? parametros[(sql.substring(0, sql.indexOf("conta"))).length()
					- (sql.substring(0, sql.indexOf("conta"))).replace(",", "").length()].toString() : "";

			// Se conta estiver no Blacklist, não insere para tratamento
			if (validarContaInseridaBlackListTratamento(cenario, conta, dynamicConnection)) {
				Log.info("Conta " + conta + " está contida no blacklist e foi descartada do tratamento");
			} else {
				pst = dynamicConnection.prepareStatement(sql);
				for (int i = 0; i < parametros.length; i++) {
					pst.setObject(i + 1, parametros[i]);
				}
				pst.executeUpdate();
			}
		} catch (Exception e) {
			Log.error("Erro ao inserir os casos de tratamento do cenario " + cenario.getCenario(), e);
			inserirCasosTratamentoCenarioContingencia(cenario, sql, parametros);
		} finally {
			close(pst);
			dynamicConnection.setUsed(false);
		}
	}

	private synchronized void inserirCasosTratamentoCenarioContingencia(CenarioVo cenario, String sql,
			Object... parametros) {
		gerarArquivoContingencia(cenario, "inserirCasosTratamento", sql, parametros);
	}

	public void buscarControleExecucao(CenarioVo cenario) throws FileNotFoundException {
		buscarDiretorioSqlUtil(cenario);
		QueryWarehouse.diretorioUtil = cenario.getSqlUtil();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;

		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			String sql = QueryWarehouse.getQuery("proativoBuscarControleExecucao");
			pst = conn.prepareStatement(sql);
			pst.setString(1, cenario.getCenario());
			pst.setString(2, cenario.getSegmento());

			rs = pst.executeQuery();

			if (rs.next()) {
				cenario.setIdCenario(rs.getInt("id_cenario"));
				cenario.setPeriodicidade(Periodicidade.valueOf(rs.getInt("periodicidade")));
				cenario.setTipoExecucao(TipoExecucao.valueOf(rs.getInt("tipo_execucao")));
				cenario.setQuantidadeThreads(rs.getInt("quantidade_threads"));
				cenario.setQuantidadeErros(rs.getInt("quantidade_erros"));
				cenario.setNomeCenario(rs.getString("descricao"));
				cenario.setDiretorioContingencia(new File(buscarParametro("diretorioContingencia")));
				cenario.setRelatorio(rs.getInt("relatorio"));
				cenario.setGerarScript(rs.getInt("gerar_script"));
				cenario.setExecutavel(rs.getBoolean("executavel"));
				cenario.setEmailCc(Arrays.asList(rs.getString("email_cc").split(";")));
				cenario.setEmailPara(Arrays.asList(rs.getString("email_para").split(";")));
				cenario.setEmailLog(Arrays.asList(rs.getString("email_log").split(";")));
				cenario.setDataInativacao(rs.getTimestamp("data_inativacao"));
			} else {
				Log.error("Erro ao obter cenario " + cenario.getCenario() + " " + cenario.getSegmento(),
						new RuntimeException("Cenario nao cadastrado."));
			}
		} catch (SQLException e) {
			Log.error("Falha ao realizar consulta de controle de execucao", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar consulta de controle de execucao", e);
		} finally {
			close(rs, pst, conn);
		}
		Log.alterarQuantidadeErros(cenario.getQuantidadeErros());
		Log.setCenario(cenario);
	}

	private void buscarDiretorioSqlUtil(CenarioVo cenario) throws FileNotFoundException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;

		try {
			String sql = "select valor from proativo_parametros where parametro = 'diretorioSqlUtil'";
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();

			if (rs.next()) {
				File file = new File(rs.getString("valor"));
				if (file.exists()) {
					cenario.setSqlUtil(file);
				} else {
					throw new FileNotFoundException(
							"Diretorio SQL util nao encontrado no servidor: " + file.getAbsolutePath());
				}
			} else {
				throw new RuntimeException("Cenario nao cadastrado: " + cenario.getCenario());
			}
		} catch (SQLException e) {
			Log.error("Falha ao realizar consulta de diretorio sqlUtil", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar consulta de diretorio sqlUtil", e);
		} finally {
			close(rs, pst, conn);
		}
	}

	public String buscarParametro(String chave) {
		String valor = "";
		Map<String, String> parametros = buscarParamentrosAplicacao();
		valor = parametros.get(chave);
		return valor;
	}

	private Map<String, String> buscarParamentrosAplicacao() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		Map<String, String> parametros = new HashMap<String, String>();

		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			String sql = QueryWarehouse.getQuery("proativoBuscarParamentrosAplicacao");
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				parametros.put(rs.getString("parametro"), rs.getString("valor"));
			}
		} catch (SQLException e) {
			Log.error("Falha ao realizar consulta de parametros", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar consulta de parametros", e);
		} finally {
			close(rs, pst, conn);
		}

		return parametros;
	}

	public List<ScriptVo> buscarScripts(CenarioVo cenario) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		List<ScriptVo> scripts = new ArrayList<ScriptVo>();

		try {
			String sql = QueryWarehouse.getQuery("proativoBuscarScripts");
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			pst = conn.prepareStatement(sql);
			pst.setObject(1, cenario.getIdExecucao());
			pst.setObject(2, cenario.getIdCenario());

			rs = pst.executeQuery();

			while (rs.next()) {
				ScriptVo script = new ScriptVo();
				script.setBase(rs.getString("base"));
				script.setComando(rs.getString("comando"));
				scripts.add(script);
			}
		} catch (SQLException e) {
			Log.error("Falha ao realizar consulta dos scripts", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar consulta dos scripts", e);
		} finally {
			close(rs, pst, conn);
		}

		return scripts;
	}

	public void limparScripts(CenarioVo cenario) {
		PreparedStatement pst = null;
		Connection conn = null;

		try {
			String sql = QueryWarehouse.getQuery("proativoLimparScripts");
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			pst = conn.prepareStatement(sql);
			pst.setObject(1, cenario.getIdExecucao());
			pst.executeUpdate();
		} catch (SQLException e) {
			Log.error("Falha ao realizar a limpeza dos scripts", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar a limpeza dos scripts", e);
		} finally {
			close(pst, conn);
		}
	}

	public String buscarSegmentosKenan(CenarioVo cenario) throws FileNotFoundException {
		buscarDiretorioSqlUtil(cenario);
		QueryWarehouse.diretorioUtil = cenario.getSqlUtil();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String retorno = null;

		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			String sql = QueryWarehouse.getQuery("proativoBuscarSegmentos");
			pst = conn.prepareStatement(sql);
			pst.setString(1, cenario.getSegmento());
			rs = pst.executeQuery();

			while (rs.next()) {
				if (retorno == null) {
					retorno = "";
					retorno = retorno + rs.getString(1);

				} else
					retorno = retorno + ", " + rs.getString(1);
			}
		} catch (SQLException e) {
			Log.error("Falha ao realizar consulta de segmentos kenan", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar consulta de segmentos kenan", e);
		} finally {
			close(rs, pst, conn);
		}

		return retorno;
	}

	/**
	 * Método para validar se determinada conta está contida no Blacklist de
	 * tratamento O blacklist deve estar com o ID_CENARIO referente ao cenário
	 * em questão ou com este valor nulo para ser considerado
	 * 
	 * @param cenario
	 * @param obj
	 * @param dynamicConnection
	 * @return
	 */
	public boolean validarContaInseridaBlackListTratamento(CenarioVo cenario, String conta,
			DynamicConnection dynamicConnection) {

		PreparedStatement pst = null;

		Boolean result = false;

		try {
			String sql = QueryWarehouse.getQuery("proativoValidarContaInseridaBlackListTratamento");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setString(1, conta);
			pst.setInt(2, cenario.getIdCenario());
			pst.setInt(3, cenario.getIdCenario());

			result = pst.executeQuery().next();

		} catch (SQLException e) {
			Log.error("Falha ao realizar consulta de controle de execucao", e);
		} catch (Exception e) {
			Log.error("Falha ao realizar consulta de controle de execucao", e);
		} finally {
			close(pst);
			dynamicConnection.setUsed(false);
		}
		return result;
	}

	public String buscarQueryAlarme(CenarioVo cenario) {
		PreparedStatement pst = null;
		String query = null;
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			String sql = QueryWarehouse.getQuery("proativoBuscarQueryAlarme");
			pst = conn.prepareStatement(sql);
			pst.setObject(1, cenario.getCenario());
			pst.setObject(2, cenario.getSegmento());

			rs = pst.executeQuery();

			if (rs.next()) {
				query = rs.getString("comando");
			}

		} catch (SQLException e) {
			Log.error("Falha ao buscar query de alarme", e);
		} catch (Exception e) {
			Log.error("Falha ao buscar query de alarme", e);
		} finally {
			close(pst, rs, conn);
		}
		return query;
	}

	public List<Object[]> buscarContasAlarme(CenarioVo cenario, String sql) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		List<Object[]> listaObj = new ArrayList<Object[]>();
		Object[] obj;

		try {
			conn = Connections.getConn(Connections.CONN_PROATIVO);
			pst = conn.prepareStatement(sql);
			pst.setObject(1, cenario.getIdExecucao());

			rs = pst.executeQuery();

			while (rs.next()) {
				obj = new String[] { rs.getString("CENARIO"), rs.getString("SEGMENTO"), rs.getString("DESCRICAO"),
						rs.getString("CONTA"), rs.getString("CICLO"), rs.getString("KENAN_DB_ID") };

				listaObj.add(obj);
			}

		} catch (SQLException e) {
			Log.error("Falha ao buscar query de alarme", e);
		} catch (Exception e) {
			Log.error("Falha ao buscar query de alarme", e);
		} finally {
			close(pst, rs, conn);
		}
		return listaObj;
	}

}
