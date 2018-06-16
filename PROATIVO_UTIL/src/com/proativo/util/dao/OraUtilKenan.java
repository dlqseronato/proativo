package com.proativo.util.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import oracle.jdbc.OracleTypes;

import com.proativo.util.QueryWarehouse;
import com.proativo.util.connection.Connections;
import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.enums.TipoElemento;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;
import com.proativo.util.vo.ComponentApiVo;
import com.proativo.util.vo.ComponentMembersVo;
import com.proativo.util.vo.ProcessamentoCicloVo;
import com.proativo.util.vo.ServiceStatusVo;
import com.proativo.util.vo.ServicoVo;

/**
 * 
 * @author G0024266 - Andre Luiz Przynyczuk
 * @author G0030353 - Rhuan Krum
 * @since 05/11/2015
 *
 */

public class OraUtilKenan extends OraUtil {

	public synchronized void inserirComponenteApi(Integer packageInstId, Integer packageInstIdServ, Integer packageId,
			Integer accountNo, Integer subscrNo, Integer subscrNoResets, Integer componentId, Timestamp dataAtivacao,
			DynamicConnection dynamicConnection) {

		ComponentApiVo component = new ComponentApiVo(packageInstId, packageInstIdServ, packageId, accountNo, subscrNo,
				subscrNoResets, componentId, dataAtivacao);

		buscarLevelComponente(component, dynamicConnection);

		if (component.getLevel() == 1) {
			apiPpAddComponentToPkgNivelConta(component, dynamicConnection);
		} else {
			apiPpAddComponentToPkgNivelInstancia(component, dynamicConnection);
		}

		buscarComponentMembers(component, dynamicConnection);

		for (ComponentMembersVo member : component.getMembers()) {

			if (member.getMemberType() == 2) {// Contrato
				apiPpInsertCmfContract(component, member, dynamicConnection);
			} else {// Produto
				apiPpInsertCmfProduct(component, member, dynamicConnection);
			}
		}

		dynamicConnection.setUsed(false);
	}

	public synchronized void apiPpInsertCmfContract(ComponentApiVo component, ComponentMembersVo member,
			DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("KenanApiPpInsertCmfContract");
			cst = dynamicConnection.prepareCall(sql);

			cst.setObject(1, component.getComponentInstId());
			cst.setObject(2, component.getComponentInstIdServ());
			cst.setObject(3, component.getComponentId());
			cst.setObject(4, component.getPackageId());
			cst.setObject(5, component.getAccountNo());
			cst.setObject(6, member.getMemberId());
			cst.setObject(7, 1);
			cst.setObject(8, component.getDataAtivacao());
			cst.setObject(9, null);
			cst.setObject(10, component.getPackageInstId());
			cst.setObject(11, component.getPackageInstIdServ());
			cst.execute();

		} catch (Exception e) {
			Log.error("Erro ao inserir contrato nivel conta: " + component.getAccountNo(), e);
		} finally {
			close(cst);
		}
	}

	public synchronized void apiPpInsertEmfContract(ComponentApiVo component, ComponentMembersVo member,
			DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("KenanApiPpInsertEmfContract");
			cst = dynamicConnection.prepareCall(sql);

			cst.setObject(1, component.getComponentInstId());
			cst.setObject(2, component.getComponentInstIdServ());
			cst.setObject(3, component.getComponentId());
			cst.setObject(4, component.getSubscrNo());
			cst.setObject(5, component.getSubscrNoResets());
			cst.setObject(6, member.getMemberId());
			cst.setObject(7, null);
			cst.setObject(8, component.getDataAtivacao());
			cst.setObject(9, null);
			cst.setObject(10, component.getPackageId());
			cst.setObject(11, component.getPackageInstId());
			cst.setObject(12, component.getPackageInstIdServ());
			cst.execute();

		} catch (Exception e) {
			Log.error("Erro ao inserir contrato nivel instancia: " + component.getAccountNo(), e);
		} finally {
			close(cst);
		}
	}

	public synchronized void apiPpInsertCmfProduct(ComponentApiVo component, ComponentMembersVo member,
			DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("kenanApiPpInsertCmfProduct");
			cst = dynamicConnection.prepareCall(sql);

			cst.setObject(1, component.getComponentInstId());
			cst.setObject(2, component.getComponentInstIdServ());
			cst.setObject(3, component.getComponentId());
			cst.setObject(4, component.getPackageId());
			cst.setObject(5, component.getAccountNo());
			cst.setObject(6, component.getAccountNo());
			cst.setObject(7, member.getMemberId());
			cst.setObject(8, 1);
			cst.setObject(9, component.getDataAtivacao());
			cst.setObject(10, 0);
			cst.setObject(11, component.getPackageInstId());
			cst.setObject(12, component.getPackageInstIdServ());
			cst.execute();

		} catch (Exception e) {
			Log.error("Erro ao inserir produto nivel conta: " + component.getAccountNo(), e);
		} finally {
			close(cst);
		}
	}

	public synchronized void apiPpInsertEmfProduct(ComponentApiVo component, ComponentMembersVo member,
			DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("kenanApiPpInsertEmfProduct");
			cst = dynamicConnection.prepareCall(sql);

			cst.setObject(1, component.getComponentInstId());
			cst.setObject(2, component.getComponentInstIdServ());
			cst.setObject(3, component.getComponentId());
			cst.setObject(4, component.getPackageId());
			cst.setObject(5, component.getSubscrNo());
			cst.setObject(6, component.getSubscrNoResets());
			cst.setObject(7, member.getMemberId());
			cst.setObject(8, 1);
			cst.setObject(9, component.getDataAtivacao());
			cst.setObject(10, 0);
			cst.setObject(11, null);
			cst.setObject(12, null);
			cst.setObject(13, null);
			cst.setObject(14, component.getPackageInstId());
			cst.setObject(15, component.getPackageInstIdServ());
			cst.execute();

		} catch (Exception e) {
			Log.error("Erro ao inserir produto nivel instancia: " + component.getAccountNo(), e);
		} finally {
			close(cst);
		}
	}

	public synchronized void apiPpAddComponentToPkgNivelConta(ComponentApiVo component,
			DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		ResultSet rs = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("kenanApiPpAddComponentToPkg");
			cst = dynamicConnection.prepareCall(sql);

			cst.setObject(1, component.getPackageInstId());
			cst.setObject(2, component.getPackageInstIdServ());
			cst.setObject(3, component.getPackageId());
			cst.setObject(4, component.getComponentId());
			cst.setObject(5, 1);
			cst.setObject(6, component.getAccountNo());
			cst.setObject(7, 0);
			cst.setObject(8, component.getDataAtivacao());
			cst.setObject(9, null);
			cst.registerOutParameter(10, OracleTypes.CURSOR);
			cst.execute();

			rs = (ResultSet) cst.getObject(10);

			if (rs.next()) {
				component.setComponentInstId(rs.getInt(":B2"));
				component.setComponentInstIdServ(rs.getInt(":B1"));
			}
		} catch (Exception e) {
			Log.error("Erro ao adicionar component nivel conta: " + component.getAccountNo(), e);
		} finally {
			close(cst, rs);
		}
	}

	public synchronized void apiPpAddComponentToPkgNivelInstancia(ComponentApiVo component,
			DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		ResultSet rs = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("kenanApiPpAddComponentToPkg");
			cst = dynamicConnection.prepareCall(sql);

			cst.setObject(1, component.getPackageInstId());
			cst.setObject(2, component.getPackageInstIdServ());
			cst.setObject(3, component.getPackageId());
			cst.setObject(4, component.getComponentId());
			cst.setObject(5, 2);
			cst.setObject(6, component.getSubscrNo());
			cst.setObject(7, component.getSubscrNoResets());
			cst.setObject(8, component.getDataAtivacao());
			cst.setObject(9, null);
			cst.registerOutParameter(10, OracleTypes.CURSOR);
			cst.execute();

			rs = (ResultSet) cst.getObject(10);

			if (rs.next()) {
				component.setComponentInstId(rs.getInt(":B2"));
				component.setComponentInstIdServ(rs.getInt(":B1"));
			}
		} catch (Exception e) {
			Log.error("Erro ao adicionar component nivel instancia: " + component.getAccountNo(), e);
		} finally {
			close(cst, rs);
		}
	}

	public synchronized void buscarLevelComponente(ComponentApiVo component, DynamicConnection dynamicConnection) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("kenanBuscarLevelComponente");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, component.getComponentId());
			rs = pst.executeQuery();

			if (rs.next()) {
				component.setLevel(rs.getInt("component_level"));
			}
		} catch (Exception e) {
			Log.error("Erro ao buscar o level do component " + component.getComponentId(), e);
		} finally {
			close(pst, rs);
		}
	}

	public synchronized void buscarComponentMembers(ComponentApiVo component, DynamicConnection dynamicConnection) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;
		List<ComponentMembersVo> members = new ArrayList<>();

		try {
			sql = QueryWarehouse.getQuery("kenanBuscarComponentMembers");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, component.getComponentId());
			rs = pst.executeQuery();

			while (rs.next()) {
				ComponentMembersVo member = new ComponentMembersVo(rs.getInt("MEMBER_TYPE"), rs.getInt("MEMBER_ID"));
				members.add(member);
			}
		} catch (Exception e) {
			Log.error("Erro ao buscar os components members do component " + component.getComponentId(), e);
		} finally {
			close(pst, rs);
		}

		component.setMembers(members);
	}

	public synchronized ProcessamentoCicloVo buscarCicloProcessamentoAtual() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		ProcessamentoCicloVo ciclo = null;

		try {
			sql = QueryWarehouse.getQuery("kenanBuscarCicloProcessamentoAtual");
			conn = Connections.getConn(Connections.CONN_KENAN_CT + 1);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				ciclo = new ProcessamentoCicloVo(rs.getString("processamento"), rs.getTimestamp("data_corte"),
						rs.getTimestamp("data_corte_anterior"));
			}
		} catch (Exception e) {
			Log.error("Erro ao obter o processamento atual", e);
		} finally {
			close(pstmt, rs, conn);
		}
		return ciclo;
	}

	public synchronized ProcessamentoCicloVo buscarCicloProcessamento(String processamento) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		ProcessamentoCicloVo ciclo = null;

		try {
			sql = QueryWarehouse.getQuery("kenanBuscarCicloProcessamento");

			conn = Connections.getConn(Connections.CONN_KENAN_CAT);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, processamento);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				ciclo = new ProcessamentoCicloVo(rs.getString("processamento"), rs.getTimestamp("data_corte"),
						rs.getTimestamp("data_corte_anterior"));
			}
		} catch (Exception e) {
			Log.error("Erro ao obter o processamento", e);
		} finally {
			close(pstmt, rs, conn);
		}
		return ciclo;
	}

	/**
	 * Busca qual o Id do banco em que o cliente está filtrando por Conta
	 * Cobrança (EXTERNAL_ID_ACCT_MAP.EXTERNAL_ID). O parâmetro é o número da
	 * conta do cliente (999...)
	 * 
	 * @param contaCobranca
	 *            Numero da conta do cliente um número iniciado por 999...
	 * @return 1 se base de dados CT1, 2 se base de dados CT2,... 0 se não
	 *         encontrado.
	 * @throws CustomerFinderException
	 */
	public synchronized Integer buscarCustDbIdByContaCobranca(String conta, DynamicConnection dynamicConnection) {
		Integer base = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;

		try {
			sql = QueryWarehouse.getQuery("kenanBuscarCustDbIdByContaCobranca");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, conta);
			rs = pst.executeQuery();

			if (rs.next()) {
				base = rs.getInt("base");
			}

		} catch (Exception e) {
			Log.error("Erro ao obter a base para a conta: " + conta, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}

		return base;
	}

	public synchronized List<Integer> buscarKenanDbIds() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<Integer> kenanDbIds = new ArrayList<Integer>();

		try {
			sql = QueryWarehouse.getQuery("kenanBuscarKenanDbIds");
			conn = Connections.getConn(Connections.CONN_KENAN_CAT);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				kenanDbIds.add(rs.getInt(1));
			}
		} catch (Exception e) {
			Log.error("Erro ao buscar as bases do Kenan", e);
		} finally {
			close(pstmt, rs, conn);
		}
		return kenanDbIds;
	}

	public synchronized void buscarCicloPorProcessamento(ProcessamentoCicloVo ciclo) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;
		List<String> ciclos = new ArrayList<String>();
		Connection conn = null;

		try {
			conn = Connections.getConn(Connections.CONN_KENAN_CAT);
			sql = QueryWarehouse.getQuery("kenanBuscarCiclosPorProcessamento");
			pst = conn.prepareStatement(sql);
			pst.setString(1, ciclo.getProcessamentoCiclo());
			pst.setString(2, ciclo.getProcessamentoCiclo());
			rs = pst.executeQuery();

			while (rs.next()) {
				ciclos.add(rs.getString("bill_period"));
			}

			ciclo.setCiclos(ciclos);
		} catch (Exception e) {
			Log.error("Erro ao obter os ciclos do processamento", e);
		} finally {
			close(pst, rs, conn);
		}
	}

	/**
	 * Insere um registro na disc component para desconectar um componente no
	 * Kenan.
	 * 
	 * @param servInst
	 *            Conta/Instancia ou Designador que possui o componente.
	 * @param packageId
	 *            Pacote do componente.
	 * @param componentInstId
	 *            Registro a ser desconectado.
	 * @param componentInstIdServ
	 *            Parametro de servidor do registro a ser desconectado.
	 * @param endDate
	 *            Data que a desconexão deve ser efetuada.
	 * @param extIdTypeInst
	 *            Tipo da instancia (Ex: 6 Voz, 7 Tnet, 9 Protect...)
	 * @param cenario
	 *            Especificação da rotina que está chamando este método.
	 */
	public synchronized void inserirGvtDiscComponent(String servInst, Integer packageId, Long componentInstId,
			Integer componentInstIdServ, Timestamp endDate, Integer extIdTypeInst, String cenario,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		try {
			String sql = QueryWarehouse.getQuery("kenanInserirGvtDiscComponent");
			removerGvtDiscComponent(componentInstId, componentInstIdServ, dynamicConnection);

			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, servInst);
			pst.setObject(2, packageId);
			pst.setObject(3, componentInstId);
			pst.setObject(4, endDate);
			pst.setObject(5, extIdTypeInst);
			pst.setObject(6, cenario);
			pst.executeUpdate();
		} catch (SQLException e) {
			Log.error("Erro ao inserir na GVT_DISC_COMPONENT", e);
		} finally {
			close(pst);
			dynamicConnection.setUsed(false);
		}
	}

	/**
	 * Insere um registro na tabela GVT_PROV_CHARGES para provisionar um
	 * componente no Kenan
	 * 
	 * @param extidAcctno
	 *            Instancia/Designador ou Conta a ser inserida.
	 * @param extIdType
	 *            Tipo de External ID.
	 * @param packageId
	 *            PackageID do componente a ser ativado.
	 * @param componentId
	 *            Componente a ser ativado.
	 * @param startDate
	 *            Data de ativação do componente.
	 * @param endDate
	 *            Data de iantivação do componente
	 * @param flag
	 *            Flag de ativação. 0 para nivel instancia e 1 para nivel conta.
	 * @param cenario
	 *            Especificação da rotina que está inserindo na tabela.
	 * @param jurisdiction
	 *            Chave de tarifação.
	 * @param units
	 *            Chave de tarifação.
	 * @param unitsType
	 *            Chave de tarifação.
	 */
	public synchronized void inserirGvtProvCharges(String extidAcctno, int extIdType, int packageId, int componentId,
			Timestamp startDate, Timestamp endDate, int flag, Integer jurisdiction, Integer units, Integer unitsType,
			String cenario, DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		try {
			String sql = QueryWarehouse.getQuery("kenanInserirGvtProvCharges");

			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, extidAcctno);
			pst.setObject(2, extIdType);
			pst.setObject(3, packageId);
			pst.setObject(4, componentId);
			pst.setObject(5, startDate);
			pst.setObject(6, endDate);
			pst.setObject(7, flag);
			pst.setObject(8, cenario);
			pst.setObject(9, jurisdiction);
			pst.setObject(10, units);
			pst.setObject(11, unitsType);

			pst.executeUpdate();

		} catch (SQLException e) {
			Log.error("Erro ao inserir na GVT_PROV_CHARGES", e);
		} finally {
			close(pst);
			dynamicConnection.setUsed(false);
		}
	}

	private synchronized void removerGvtDiscComponent(Long componentInstId, Integer componentInstIdServ,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;

		try {
			String sql = QueryWarehouse.getQuery("kenanRemoverGvtDiscComponent");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, componentInstId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pst);
		}
	}

	public synchronized void gerarArquivoSQL(CenarioVo cenario, String arquivoSql, Object... parametros) {
		String descricao = "tratamento";

		String sql = "";

		if (arquivoSql.toLowerCase().contains("insert ") || arquivoSql.toLowerCase().contains("delete ")
				|| arquivoSql.toLowerCase().contains("update ") || arquivoSql.toLowerCase().contains("select ")) {
			sql = arquivoSql;
		} else {
			sql = QueryWarehouse.getQuery(arquivoSql);
		}

		gerarArquivoContingencia(cenario, descricao, sql, parametros);
	}

	/**
	 * Metodo para desconectar Produtos e Contratos
	 * 
	 * @param componentInstId,
	 *            componentInstIdServ, subscrNo, dataInativacao, alteradoPor,
	 *            conexoes
	 * @return Retorna verdadeiro se os elementos foram desconectados
	 */
	public synchronized boolean desconectarElementos(CenarioVo cenario, Long componentInstId,
			Integer componentInstIdServ, TipoElemento tipoElemento, Integer subscrNo, Timestamp dataInativacao,
			String alteradoPor, DynamicConnection dynamicConnectionCat, DynamicConnection dynamicConnectionCt) {
		boolean resultado = false;

		try {

			switch (tipoElemento) {
			case CONTA:
				if (desconectarProduto(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, alteradoPor, dynamicConnectionCt)) {
					resultado = true;
				}
				break;

			case INSTANCIA:
				if (desconectarProduto(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, alteradoPor, dynamicConnectionCt)) {
					resultado = true;
				}
				break;

			case CONTRATO:
				if (desconectarContrato(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, dynamicConnectionCt)) {
					resultado = true;
				}
				break;

			case ATRELADO:
				if (desconectarAtrelado(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, alteradoPor, dynamicConnectionCt)) {
					resultado = true;
				}
				break;
			}

		} catch (Exception e) {
			Log.error("Erro ao desconectar Elementos. ComponentInstId: " + componentInstId, e);
			resultado = false;

		}

		return resultado;
	}

	/**
	 * Metodo para desconectar Produtos e Contratos
	 * 
	 * @param componentInstId,
	 *            componentInstIdServ, tipoElemento, dataInativacao,
	 *            alteradoPor, conexao
	 * @return Retorna verdadeiro se os elementos foram desconectados
	 */
	public synchronized boolean desconectarElementos(CenarioVo cenario, Long componentInstId,
			Integer componentInstIdServ, TipoElemento tipoElemento, Timestamp dataInativacao, String alteradoPor,
			DynamicConnection dynamicConnectionCt) {
		boolean resultado = false;

		try {

			switch (tipoElemento) {

			case CONTA:
			case INSTANCIA:
				if (desconectarProduto(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, alteradoPor, dynamicConnectionCt)) {
					resultado = true;
				}
				break;

			case CONTRATO:
				if (desconectarContrato(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, dynamicConnectionCt)) {
					resultado = true;
				}
				break;

			case ATRELADO:
				if (desconectarAtrelado(cenario, componentInstId, componentInstIdServ, tipoElemento.getValue(),
						dataInativacao, alteradoPor, dynamicConnectionCt)) {
					resultado = true;
				}
				break;
			}

		} catch (Exception e) {
			Log.error("Erro ao desconectar Elementos. ComponentInstId: " + componentInstId, e);
		}

		dynamicConnectionCt.setUsed(false);
		return resultado;
	}

	public synchronized boolean desconectarServico(CenarioVo cenario, Integer subscrNo, Timestamp dataInativacao, String alteradoPor, DynamicConnection dynamicConnectionCat, DynamicConnection dynamicConnectionCt) {
		boolean resultado = true;
		ServicoVo servico = new ServicoVo();
		servico.setDataDesconexao(dataInativacao);
		servico.setSubscrNo(subscrNo);
		buscarInformacoesInstancia(servico, dynamicConnectionCat);
		
		try {

			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarService", dataInativacao,
					alteradoPor, subscrNo);
			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarCustomerIdEquipMap", dataInativacao,
					subscrNo);
			executarScriptTratamento(cenario, dynamicConnectionCat, "kenanDesconectarExternalIdEquipMap",
					dataInativacao, subscrNo, servico.getInstancia(), servico.getDataAtivacao());

			desconectarServiceStatus(cenario, subscrNo, dataInativacao, alteradoPor, dynamicConnectionCt);

			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarServiceBilling", dataInativacao,
					subscrNo);
			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarServiceAddresssAssoc",
					dataInativacao, subscrNo);

		} catch (Exception e) {
			Log.error("Erro ao desconectar servico: SubscrNo: " + subscrNo, e);
			resultado = false;
		}

		dynamicConnectionCt.setUsed(false);
		dynamicConnectionCat.setUsed(false);

		return resultado;
	}

	public synchronized boolean desconectarServico(CenarioVo cenario, Integer subscrNo, String instancia,
			Timestamp dataInativacao, String alteradoPor, DynamicConnection dynamicConnectionCat,
			DynamicConnection dynamicConnectionCt) {
		boolean resultado = true;
		try {

			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarService", dataInativacao,
					alteradoPor, subscrNo);
			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarCustomerIdEquipMap", dataInativacao,
					subscrNo);
			executarScriptTratamento(cenario, dynamicConnectionCat, "kenanDesconectarExternalIdEquipMapPorInstancia",
					dataInativacao, subscrNo, instancia);

			desconectarServiceStatus(cenario, subscrNo, dataInativacao, alteradoPor, dynamicConnectionCt);

			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarServiceBilling", dataInativacao,
					subscrNo);
			executarScriptTratamento(cenario, dynamicConnectionCt, "kenanDesconectarServiceAddresssAssoc",
					dataInativacao, subscrNo);

		} catch (Exception e) {
			Log.error("Erro ao desconectar servico: SubscrNo: " + subscrNo, e);
			resultado = false;
		}

		dynamicConnectionCt.setUsed(false);
		dynamicConnectionCat.setUsed(false);

		return resultado;
	}

	private synchronized boolean desconectarProduto(CenarioVo cenario, Long componentInstId,
			Integer componentInstIdServ, Integer tipoElemento, Timestamp dataInativacao, String alteradoPor,
			DynamicConnection dynamicConnection) {
		boolean resultado = true;
		try {

			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCmfPackageComponent", dataInativacao,
					componentInstId, componentInstIdServ);
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCmfComponentElement", dataInativacao,
					componentInstId, componentInstIdServ, tipoElemento);
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarProduct", dataInativacao,
					dataInativacao, alteradoPor, componentInstId, componentInstIdServ);
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarProductChargeMap", dataInativacao, componentInstId, componentInstIdServ );
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarProductRateKey", dataInativacao, componentInstId, componentInstIdServ );

		} catch (Exception e) {
			Log.error("Erro ao desconectar produto: ComponentInstId: " + componentInstId, e);
			resultado = false;
		}
		return resultado;
	}

	private synchronized boolean desconectarAtrelado(CenarioVo cenario, Long componentInstId,
			Integer componentInstIdServ, Integer tipoElemento, Timestamp dataInativacao, String alteradoPor,
			DynamicConnection dynamicConnection) {
		boolean resultado = true;
		try {

			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCmfPackageComponent", dataInativacao,
					componentInstId, componentInstIdServ);
			
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCmfComponentElementAtrelado", dataInativacao,
					componentInstId, componentInstIdServ);
			
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarProduct", dataInativacao,
					dataInativacao, alteradoPor, componentInstId, componentInstIdServ);
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCustomerContract", dataInativacao,
					componentInstId, componentInstIdServ);

			// executarScriptTratamento(cenario, dynamicConnection,
			// "kenanDesconectarProductChargeMap", dataInativacao,
			// componentInstId, componentInstIdServ );

		} catch (Exception e) {
			Log.error("Erro ao desconectar produto: ComponentInstId: " + componentInstId, e);
			resultado = false;
		}
		return resultado;
	}

	private synchronized boolean desconectarContrato(CenarioVo cenario, Long componentInstId,
			Integer componentInstIdServ, Integer tipoElemento, Timestamp dataInativacao,
			DynamicConnection dynamicConnection) {
		boolean resultado = true;

		try {
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCmfPackageComponent", dataInativacao,
					componentInstId, componentInstIdServ);
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCmfComponentElement", dataInativacao,
					componentInstId, componentInstIdServ, tipoElemento);
			executarScriptTratamento(cenario, dynamicConnection, "kenanDesconectarCustomerContract", dataInativacao,
					componentInstId, componentInstIdServ);
		} catch (Exception e) {
			Log.error("Erro ao desconectar contrato. ComponentInstId: " + componentInstId, e);
			resultado = false;
		}

		return resultado;
	}

	public synchronized boolean possuiElementosAtivos(Integer accountNo, Timestamp dataInativacao,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanBuscarElementosAtivos");

			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, dataInativacao);
			pst.setObject(2, accountNo);
			pst.setObject(3, accountNo);
			pst.setObject(4, dataInativacao);

			rs = pst.executeQuery();

			if (rs.next()) {
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao buscar elementos ativos. AccountNo: " + accountNo, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);

		}

		return resultado;
	}

	public synchronized boolean existeOrdemPendente(String ordem, Integer accountNo,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteOrdemPendente");

			pst = dynamicConnection.prepareStatement(sql);
			pst.setInt(1, accountNo);
			pst.setString(2, ordem);

			rs = pst.executeQuery();

			if (rs.next()) {
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao buscar ordem. Ordem: " + ordem, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	public synchronized boolean existeOrdemDesconexaoPendente(String ordem, Integer accountNo,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteOrdemDesconexaoPendente");

			pst = dynamicConnection.prepareStatement(sql);
			pst.setInt(1, accountNo);
			pst.setString(2, ordem);

			rs = pst.executeQuery();

			if (rs.next()) {
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao buscar ordem. AccountNo: " + accountNo, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	public synchronized boolean existeOrdemDesconexaoConcluida(String ordem, Integer accountNo,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteOrdemDesconexaoConcluida");

			pst = dynamicConnection.prepareStatement(sql);
			pst.setString(1, ordem);
			pst.setInt(2, accountNo);

			rs = pst.executeQuery();

			if (rs.next()) {
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao buscar ordem. AccountNo: " + accountNo, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	public synchronized boolean marcarNoBillCdr(CenarioVo cenario, Integer msgId, Integer msgId2, Integer msgIdServ,
			String anotacao, DynamicConnection dynamicConnection) {
		boolean resultado = true;
		try {
			executarScriptTratamento(cenario, dynamicConnection, "kenanMarcarNoBillCdr", anotacao, msgId, msgId2,
					msgIdServ);

		} catch (Exception e) {
			Log.error(
					"Erro ao marcar noBill no CDR: MsgId: " + msgId + " MsgId2: " + msgId2 + " MsgIdServ: " + msgIdServ,
					e);
			resultado = false;
		}
		return resultado;
	}

	public synchronized boolean marcarNoBillNrc(CenarioVo cenario, Long viewId, String anotacao, String alteradoPor,
			DynamicConnection dynamicConnection) {
		boolean resultado = true;
		try {
			executarScriptTratamento(cenario, dynamicConnection, "kenanMarcarNoBillNrc", anotacao, alteradoPor, viewId);

		} catch (Exception e) {
			Log.error("Erro ao marcar noBill na NRC: viewId: " + viewId, e);
			resultado = false;
		}
		return resultado;
	}

	public synchronized boolean atualizarDataUltimoFaturamentoProduto(CenarioVo cenario, Integer trackingId,
			Integer trackingIdServ, Timestamp data, DynamicConnection dynamicConnection) {
		boolean resultado = true;
		try {
			executarScriptTratamento(cenario, dynamicConnection, "kenanAtualizarDataUltimoFaturamentoProduto", data,
					trackingId, trackingIdServ);

		} catch (Exception e) {
			Log.error("Erro ao atualizar data do ultimo faturamento do trackingId " + trackingId, e);
			resultado = false;
		}
		return resultado;
	}

	private synchronized boolean desconectarServiceStatus(CenarioVo cenario, Integer subscrNo, Timestamp dataInativacao,
			String alteradoPor, DynamicConnection dynamicConnection) {
		boolean resultado = true;

		try {

			if (possuiServiceStatusInativo(subscrNo, dynamicConnection)) {
				ServiceStatusVo serviceStatusInativo = new ServiceStatusVo(subscrNo);
				buscarServiceStatusInativo(serviceStatusInativo, dynamicConnection);
				serviceStatusInativo.setChgWho(alteradoPor);

				if (possuiServiceStatusAtivo(subscrNo, dynamicConnection)) {
					ServiceStatusVo serviceStatusAtivo = new ServiceStatusVo(subscrNo);
					buscarServiceStatusAtivo(serviceStatusAtivo, dynamicConnection);
					serviceStatusAtivo.setChgWho(alteradoPor);

					if (serviceStatusAtivo.getInactiveDt().after(dataInativacao)) {
						serviceStatusAtivo.setInactiveDt(dataInativacao);

						executarScriptTratamento(cenario, dynamicConnection, "kenanAtualizarServiceStatus",
								serviceStatusAtivo.getChgWho(), serviceStatusAtivo.getActiveDt(),
								serviceStatusAtivo.getInactiveDt(), serviceStatusAtivo.getSubscrNo(),
								serviceStatusAtivo.getStatusId());

						serviceStatusInativo.setActiveDt(dataInativacao);

						if (serviceStatusInativo.getActiveDt().equals(serviceStatusAtivo.getActiveDt())) {
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(serviceStatusInativo.getActiveDt().getTime());
							c.add(Calendar.SECOND, 1);
							Timestamp dataNova = new Timestamp(c.getTimeInMillis());
							serviceStatusInativo.setActiveDt(dataNova);
						}
					}

					executarScriptTratamento(cenario, dynamicConnection, "kenanAtualizarServiceStatus",
							serviceStatusInativo.getChgWho(), serviceStatusInativo.getActiveDt(), null,
							serviceStatusInativo.getSubscrNo(), serviceStatusInativo.getStatusId());
				}
			} else {
				ServiceStatusVo serviceStatusAtivo = new ServiceStatusVo(subscrNo);
				buscarServiceStatusAtivo(serviceStatusAtivo, dynamicConnection);
				serviceStatusAtivo.setChgWho(alteradoPor);
				serviceStatusAtivo.setInactiveDt(dataInativacao);

				executarScriptTratamento(cenario, dynamicConnection, "kenanAtualizarServiceStatus",
						serviceStatusAtivo.getChgWho(), serviceStatusAtivo.getActiveDt(),
						serviceStatusAtivo.getInactiveDt(), serviceStatusAtivo.getSubscrNo(),
						serviceStatusAtivo.getStatusId());

				ServiceStatusVo serviceStatusInativo = new ServiceStatusVo(subscrNo);
				serviceStatusInativo.setActiveDt(dataInativacao);

				if (serviceStatusInativo.getActiveDt().equals(serviceStatusAtivo.getActiveDt())) {
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(serviceStatusInativo.getActiveDt().getTime());
					c.add(Calendar.SECOND, 1);
					Timestamp dataNova = new Timestamp(c.getTimeInMillis());
					serviceStatusInativo.setActiveDt(dataNova);
				}

				serviceStatusInativo.setChgWho(alteradoPor);
				serviceStatusInativo.setInactiveDt(null);
				serviceStatusInativo.setStatusId(2);
				serviceStatusInativo.setStatusReasonId(serviceStatusAtivo.getStatusReasonId());
				serviceStatusInativo.setStatusTypeId(serviceStatusAtivo.getStatusTypeId());
				serviceStatusInativo.setSubscrNoResets(serviceStatusAtivo.getSubscrNoResets());
				serviceStatusInativo.setChgDt(new Timestamp(new Date().getTime()));

				executarScriptTratamento(cenario, dynamicConnection, "kenanInserirServiceStatus",
						serviceStatusInativo.getSubscrNo(), serviceStatusInativo.getSubscrNoResets(),
						serviceStatusInativo.getActiveDt(), serviceStatusInativo.getStatusTypeId(),
						serviceStatusInativo.getStatusId(), serviceStatusInativo.getStatusReasonId(),
						serviceStatusInativo.getChgWho(), serviceStatusInativo.getChgDt(),
						serviceStatusInativo.getInactiveDt());
			}

		} catch (Exception e) {
			Log.error("Erro ao desconectar/atualizar a Service Status. SubscrNo: " + subscrNo, e);
			resultado = false;
		}

		return resultado;
	}

	private synchronized void buscarServiceStatusAtivo(ServiceStatusVo serviceStatus,
			DynamicConnection dynamicConnection) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = QueryWarehouse.getQuery("kenanBuscarServiceStatusAtivo");

		pst = dynamicConnection.prepareStatement(sql);
		pst.setInt(1, serviceStatus.getSubscrNo());

		rs = pst.executeQuery();

		if (rs.next()) {
			serviceStatus.setActiveDt(rs.getTimestamp("active_dt"));
			serviceStatus.setChgDt(rs.getTimestamp("chg_dt"));
			serviceStatus.setStatusId(rs.getInt("status_id"));
			serviceStatus.setStatusReasonId(rs.getInt("status_reason_id"));
			serviceStatus.setStatusTypeId(rs.getInt("status_type_id"));
			serviceStatus.setSubscrNo(rs.getInt("subscr_no"));
			serviceStatus.setSubscrNoResets(rs.getInt("subscr_no_resets"));
			serviceStatus.setInactiveDt(rs.getTimestamp("inactive_dt"));
		}

		close(rs, pst);
	}

	private synchronized void buscarServiceStatusInativo(ServiceStatusVo serviceStatus,
			DynamicConnection dynamicConnection) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = QueryWarehouse.getQuery("kenanBuscarServiceStatusInativo");

		pst = dynamicConnection.prepareStatement(sql);
		pst.setInt(1, serviceStatus.getSubscrNo());

		rs = pst.executeQuery();

		if (rs.next()) {
			serviceStatus.setActiveDt(rs.getTimestamp("active_dt"));
			serviceStatus.setChgDt(rs.getTimestamp("chg_dt"));
			serviceStatus.setStatusId(rs.getInt("status_id"));
			serviceStatus.setStatusReasonId(rs.getInt("status_reason_id"));
			serviceStatus.setStatusTypeId(rs.getInt("status_type_id"));
			serviceStatus.setSubscrNo(rs.getInt("subscr_no"));
			serviceStatus.setSubscrNoResets(rs.getInt("subscr_no_resets"));
		}

		close(rs, pst);
	}

	private synchronized boolean possuiServiceStatusInativo(Integer subscrNo, DynamicConnection dynamicConnection)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = QueryWarehouse.getQuery("kenanBuscarServiceStatusInativo");
		boolean resultado = false;

		pst = dynamicConnection.prepareStatement(sql);
		pst.setInt(1, subscrNo);

		rs = pst.executeQuery();

		if (rs.next()) {
			resultado = true;
		}

		close(rs, pst);

		return resultado;
	}

	private synchronized boolean possuiServiceStatusAtivo(Integer subscrNo, DynamicConnection dynamicConnection)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = QueryWarehouse.getQuery("kenanBuscarServiceStatusAtivo");
		boolean resultado = false;

		pst = dynamicConnection.prepareStatement(sql);
		pst.setInt(1, subscrNo);

		rs = pst.executeQuery();

		if (rs.next()) {
			resultado = true;
		}

		close(rs, pst);

		return resultado;
	}

	public synchronized boolean cancelarOrdem(CenarioVo cenario, String ordem, Integer accountNo,
			DynamicConnection dynamicConnection) {
		boolean resultado = true;
		try {
			executarScriptTratamento(cenario, dynamicConnection, "kenanCancelarOrdemOrdServiceOrder", ordem, accountNo);
			executarScriptTratamento(cenario, dynamicConnection, "kenanCancelarOrdemOrdOrder", ordem, accountNo);
		} catch (Exception e) {
			Log.error("Erro ao cancelar ordem: " + ordem, e);
			resultado = false;
		}

		dynamicConnection.setUsed(false);
		return resultado;
	}

	public synchronized void aprovisionaNrcMulta(CenarioVo cenario, Integer accountNo, Integer subscrNo,
			Integer subscrNoResets, Integer typeIdNrc, Integer valorNrc, Timestamp dataNrc, String anotacao,
			Integer openItemId, DynamicConnection dynamicConnection) {

		CallableStatement cst = null;
		String sql = null;

		try {
			if (cenario.getGerarScript() == 0) {
				sql = QueryWarehouse.getQuery("kenanAprovisionarNrcMulta");
				cst = dynamicConnection.prepareCall(sql);

				cst.setObject(1, accountNo);
				cst.setObject(2, subscrNo);
				cst.setObject(3, subscrNoResets);
				cst.setObject(4, typeIdNrc);
				cst.setObject(5, valorNrc);
				cst.setObject(6, dataNrc);
				cst.setObject(7, anotacao);
				cst.setObject(10, openItemId);

				cst.registerOutParameter(8, Types.VARCHAR);
				cst.registerOutParameter(9, Types.VARCHAR);

				// Integer trackingId = cst.getInt(8);
				// Integer trackingIdServ = cst.getInt(9);

				cst.execute();
			} else {
				sql = QueryWarehouse.getQuery("kenanPlAprovisionarNrcMulta");
				gerarScriptSQL(cenario, sql, dynamicConnection.getConnectionName() + "_NRC", accountNo, subscrNo,
						subscrNoResets, typeIdNrc, valorNrc, dataNrc, anotacao, openItemId);
			}

		} catch (SQLException e) {

		} catch (Exception e) {
			Log.error("Falha ao realizar aprovisionamento de NRC no Kenan", e);
		} finally {
			if (cenario.getGerarScript() == 0) {
				close(cst);
			}

			dynamicConnection.setUsed(false);
		}
	}

	public synchronized boolean existeMultaFidelidade(Integer accountNo, DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteMultaFidelidade");

			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, accountNo);

			rs = pst.executeQuery();

			if (rs.next()) {
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao buscar Multa de fidelidade. AccountNo: " + accountNo, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	public boolean existeRegistroPendenteSautorotProvCharges(String extidAcctno, int extIdType, int packageId,
			int componentId, Timestamp startDate, Timestamp endDate, String rotina,
			DynamicConnection dynamicConnection) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteRegistroPendenteSautorotProvChargesFull");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setString(1, extidAcctno);
			pst.setInt(2, extIdType);
			pst.setInt(3, packageId);
			pst.setInt(4, componentId);
			pst.setTimestamp(5, startDate);
			pst.setTimestamp(6, endDate);
			pst.setString(7, rotina);
			
			rs = pst.executeQuery();
			
			if(rs.next()){
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		} finally {
			close(rs, pst);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	public boolean existeRegistroPendenteSautorotProvCharges(String rotina) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Connection> conns = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteRegistroPendenteSautorotProvCharges");

			conns = Connections.getConnectionsKenan();

			for (Connection conn : conns) {
				pst = conn.prepareStatement(sql);
				pst.setString(1, rotina);

				rs = pst.executeQuery();

				// Utiliza-se if pois mais de uma vlidação é realizada
				if (rs.next()) {
					resultado = true;
				}

				close(rs, pst, conn);
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		}

		return resultado;
	}

	public boolean existeRegistroPendenteSautorotDiscComponent(String rotina) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Connection> conns = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteRegistroPendenteSautorotDiscComponent");

			conns = Connections.getConnectionsKenan();

			for (Connection conn : conns) {
				pst = conn.prepareStatement(sql);
				pst.setString(1, rotina);

				rs = pst.executeQuery();

				// Utiliza-se if pois mais de uma vlidação é realizada
				if (rs.next()) {
					resultado = true;
				}

				close(rs, pst, conn);
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		}

		return resultado;
	}
	public boolean existeRegistroPendenteDiscComponent(String servInst, Integer packageId, Long componentInstId,
			 Timestamp endDate, Integer extIdTypeInst, String rotina,
			DynamicConnection dynamicConnection) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteRegistroPendenteSautorotDiscComponentFull");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setString(1, servInst);
			pst.setInt(2, packageId);
			pst.setLong(3, componentInstId);
			pst.setTimestamp(4, endDate);
			pst.setInt(5, extIdTypeInst);
			pst.setString(6, rotina);
			
			rs = pst.executeQuery();
			
			if(rs.next()){
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		} finally {
			close(rs, pst);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	public boolean existeRegistroPendenteSautorotCeaseAccount(String rotina) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Connection> conns = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteRegistroPendenteSautorotCeaseAccount");

			conns = Connections.getConnectionsKenan();

			for (Connection conn : conns) {
				pst = conn.prepareStatement(sql);
				pst.setString(1, rotina);

				rs = pst.executeQuery();

				// Utiliza-se if pois mais de uma validação é realizada
				if (rs.next()) {
					resultado = true;
				}

				close(rs, pst, conn);
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		}

		return resultado;
	}
	public boolean existeRegistroPendenteSautorotCeaseAccount(String servInst, String externalAcctId, Timestamp disconectDate,
			Integer disconectAccount,  Integer ExtIdTypeInst, String rotina,
			DynamicConnection dynamicConnection) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("kenanExisteRegistroPendenteSautorotCeaseAccountFull");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setString(1, servInst);
			pst.setString(2, externalAcctId);
			pst.setTimestamp(3, disconectDate);
			pst.setInt(4, disconectAccount);
			pst.setInt(5, ExtIdTypeInst);
			pst.setString(6, rotina);
			
			rs = pst.executeQuery();
			
			if(rs.next()){
				resultado = true;
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		} finally {
			close(rs, pst);
			dynamicConnection.setUsed(false);
		}

		return resultado;
	}

	/**
	 * @author 80480943 - André Luiz S. Teotônio
	 * @author Vitor Nascimento
	 * @since 07/04/2017
	 * 
	 *        Após aguardar o sautorot na ThreadAction será chamada a
	 *        ThreadValidation, onde esse método deve ser executado para
	 *        realizar a validação do tratamento, e setar o
	 *        ObjetoUtilVo.resultado com seu retorno (1 ou 0).
	 * 
	 * @param dynamicConnection
	 * @param newExtIdAcctNo
	 * @param rotina
	 * @return
	 */
	public synchronized Integer validacaoSautorotProvCharges(DynamicConnection dynamicConnection, String newExtIdAcctNo,
			String rotina) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;
		Integer retorno = 0;
		try {
			sql = QueryWarehouse.getQuery("kenanValidacaoSautorotProvCharges");
			pst = dynamicConnection.prepareStatement(sql);

			if (newExtIdAcctNo != null) {
				pst.setString(1, newExtIdAcctNo);
				pst.setString(2, rotina);

				rs = pst.executeQuery();

				if (rs.next()) {

					retorno = rs.getInt("retorno");

				}
			}
		} catch (SQLException e) {
			Log.error("Falha ao levantar os casos no Kenan", e);
		} catch (Exception e) {
			Log.error("Falha ao levantar os casos no Kenan", e);
		} finally {
			close(rs, pst);
			dynamicConnection.setUsed(false);
		}
		return retorno;
	}

	/**
	 * 
	 * @author 80480943 - André Luiz S. Teotônio
	 * @since 08/04/2017
	 * 
	 * @param servInst
	 *            Instância (linha telefônica ou dados)
	 * @param externalIdAcctId
	 *            9999xxx
	 * @param disconectDate
	 *            Data que os componentes serão inativados
	 * @param disconectAccount
	 *            0 componentes, 1 componentes e instancia
	 * @param runStatus
	 *            Se 99, executa a cada 15 minutos, se -1 ERRO
	 * @param ExtIdTypeInst
	 *            6 = linha telefônica 7 = dados 10 = TV
	 * @param rotina
	 *            Descreve o processo de tratamento
	 * @param dynamicConnection
	 */
	public synchronized void inserirGvtCeasseAccount(String servInst, String externalAcctId, Date disconectDate,
			Integer disconectAccount, Integer runStatus, Integer ExtIdTypeInst, String rotina,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		String sql = null;

		try {
			pst = null;
			sql = QueryWarehouse.getQuery("kenanInserirGvtCeasseAccount");

			removerGvtCeaseAccount(servInst, externalAcctId, dynamicConnection);

			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, servInst);
			pst.setObject(2, externalAcctId);
			pst.setObject(3, disconectDate);
			pst.setObject(4, disconectAccount);
			pst.setObject(5, runStatus);
			pst.setObject(6, ExtIdTypeInst);
			pst.setObject(7, rotina);
			pst.executeUpdate();
		} catch (SQLException e) {
			Log.error("Falha de SQL ao levantar os casos no Kenan", e);
		} catch (Exception e) {
			Log.error("Falha ao levantar os casos no Kenan", e);
		} finally {
			close(pst);
			dynamicConnection.setUsed(false);
		}

	}

	/**
	 * @author 80480943 - André Luiz S. Teotônio
	 * @since 08/04/2017
	 * 
	 * @param servInst
	 *            Instância que deve ser removida da cease_account
	 * @param externalAcctId
	 *            Conta a qual pertence a instância
	 * @param dynamicConnection
	 */
	private synchronized void removerGvtCeaseAccount(String servInst, String externalAcctId,
			DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		String sql = null;
		try {
			sql = QueryWarehouse.getQuery("kenanRemoverGvtCeaseAccount");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, servInst);
			pst.setObject(2, externalAcctId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pst);
			dynamicConnection.setUsed(false);
		}
	}

	/**
	 * @author 80480943 - André Luiz S. Teotônio
	 * @since 08/04/2017
	 * 
	 *        Após aguardar o sautorot na ThreadAction será chamada a
	 *        ThreadValidation, onde esse método deve ser executado para
	 *        realizar a validação do tratamento, e setar o
	 *        ObjetoUtilVo.resultado com seu retorno (1 ou 0).
	 * 
	 * @param dynamicConnection
	 * @param newServInst
	 * @param newExtIdAcctNo
	 * @param rotina
	 * @return
	 */
	public synchronized Integer validacaoSautorotGvtCeaseAccount(DynamicConnection dynamicConnection,
			String newServInst, String newExternalAcctId, String rotina) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;
		Integer retorno = 0;
		try {
			sql = QueryWarehouse.getQuery("kenanValidacaoSautorotGvtCeaseAccount");
			pst = dynamicConnection.prepareStatement(sql);

			if (newExternalAcctId != null && newServInst != null) {
				pst.setString(1, newServInst);
				pst.setString(2, newExternalAcctId);
				pst.setString(3, rotina);
				rs = pst.executeQuery();
				if (rs.next())
					retorno = rs.getInt("retorno");
			}
		} catch (SQLException e) {
			Log.error("Falha ao levantar os casos no Kenan", e);
		} catch (Exception e) {
			Log.error("Falha ao levantar os casos no Kenan", e);
		} finally {
			close(rs, pst);
			dynamicConnection.setUsed(false);
		}
		return retorno;
	}

	/**
	 * @author 80480943 - André Luiz S. Teotônio
	 * @since 04/06/2017
	 * 
	 *        Após aguardar o sautorot na ThreadAction será chamada a
	 *        ThreadValidation, onde esse método deve ser executado para
	 *        realizar a validação do tratamento, e setar o
	 *        ObjetoUtilVo.resultado com seu retorno (1 ou 0).
	 * 
	 * @param dynamicConnection
	 * @param newServInst
	 * @param newComponentInstId
	 * @param rotina
	 * @return
	 */
	public synchronized Integer validacaoSautorotDiscComponent(DynamicConnection dynamicConnection, String newServInst,
			Long newComponentInstId, String rotina) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = null;
		Integer retorno = 0;
		try {
			sql = QueryWarehouse.getQuery("kenanValidacaoSautorotDiscComponent");
			pst = dynamicConnection.prepareStatement(sql);

			if (newComponentInstId != null && newServInst != null) {
				pst.setString(1, newServInst);
				pst.setLong(2, newComponentInstId);
				pst.setString(3, rotina);
				rs = pst.executeQuery();
				if (rs.next())
					retorno = rs.getInt("retorno");
			}
		} catch (SQLException e) {
			Log.error("Falha de SQL ao validar sautorot DiscCompnent no Kenan", e);
		} catch (Exception e) {
			Log.error("Falha so validar sautorot DiscCompnent no Kenan", e);
		} finally {
			close(rs, pst);
			dynamicConnection.setUsed(false);
		}
		return retorno;
	}
	
	
	public void buscarInformacoesInstancia(ServicoVo servico, DynamicConnection dynamicConnection) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String sql = QueryWarehouse.getQuery("kenanBuscarInformacoesInstancia");
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, servico.getSubscrNo());
			
			rs = pst.executeQuery();
			
			if(rs.next()){
				servico.setDataAtivacao(rs.getTimestamp("data_ativacao"));
				servico.setInstancia(rs.getString("instancia"));
			}

		} catch (SQLException e) {
			Log.error("Erro ao realizar consulta no Kenan", e);
		} finally {
			close(rs, pst);
		}
	}
}
