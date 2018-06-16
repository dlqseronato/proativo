package com.proativo.util.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.proativo.util.QueryWarehouse;
import com.proativo.util.connection.DynamicConnection;
import com.proativo.util.log.Log;

public class OraUtilSiebel5 extends OraUtil {

	
	public boolean isContaSiebel5Valida(String conta, DynamicConnection dynamicConnection) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean resultado = false;

		try {
			String sql = QueryWarehouse.getQuery("siebel5isContaSiebel5Valida");
			
			pst = dynamicConnection.prepareStatement(sql);
			pst.setString(1, conta);

			rs = pst.executeQuery();
			
			if(rs.next()){
				resultado = true;
			}
			
		} catch (SQLException e) {
			Log.error("Erro ao buscar conta no Siebel 5. Conta: " + conta, e);
		} finally {
			close(pst, rs);
			dynamicConnection.setUsed(false);
		}
		
		return resultado;
	}
}
