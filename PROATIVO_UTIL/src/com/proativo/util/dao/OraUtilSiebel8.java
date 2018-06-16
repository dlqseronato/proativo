package com.proativo.util.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.proativo.util.QueryWarehouse;
import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.enums.TipoOferta;
import com.proativo.util.log.Log;
import com.proativo.util.vo.MultaVo;

public class OraUtilSiebel8 extends OraUtil {
	
	public MultaVo buscarMultaFidelidade(String conta, String ordem, DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		MultaVo multa = new MultaVo();

		try {
			String sql = QueryWarehouse.getQuery("siebel8BuscarMultaFidelidade");
			
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, conta);
			pst.setObject(2, ordem);

			rs = pst.executeQuery();
			
			if(rs.next()){
				multa.setTipo(rs.getString("tipo"));
				multa.setValor(rs.getDouble("valor"));
				multa.setPossuiMulta(true);
			}
			
		} catch (SQLException e) {
			Log.error("Erro ao buscar Multa de fidelidade no Siebel. Conta: " + conta, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}
		
		return multa;
	}
	
	public TipoOferta buscarPortfolio(String conta, DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		TipoOferta tipoOferta = TipoOferta.NENHUMA;

		try {
			String sql = QueryWarehouse.getQuery("siebel8BuscarPortfolio");
			
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, conta);

			rs = pst.executeQuery();
			
			if(rs.next()){
				tipoOferta = TipoOferta.equals(rs.getString("tipo_oferta"));
			}
			
		} catch (SQLException e) {
			Log.error("Erro ao buscar portfolio no Siebel. Conta: " + conta, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}
		
		return tipoOferta;
	}
	
	public boolean isContaSiebel8Valida(String conta, DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("siebel8isContaSiebel8Valida");
			
			pst = dynamicConnection.prepareStatement(sql);
			pst.setObject(1, conta);

			rs = pst.executeQuery();
			
			if(rs.next()){
				resultado = true;
			}
			
		} catch (SQLException e) {
			Log.error("Erro ao buscar conta no Siebel 8. Conta: " + conta, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}
		
		return resultado;
	}

}
