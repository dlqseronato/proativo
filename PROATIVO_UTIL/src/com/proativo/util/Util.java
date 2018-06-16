package com.proativo.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.proativo.util.vo.ObjetoUtilVo;



public class Util {
	
	public static final Boolean VALORES_NORMAIS_SQL = false;
	public static final Boolean VALORES_COMO_STRING_SQL = true;
	
	
	/**
	 * Usar a lista de retorno para execuar a mesma query mais de uma vez passando cada linha da lista na c�usula IN.
	 * 
	 * @author 80480943 - Andr� Luiz S. Teot�nio
	 * @since 18/03/2017
	 * @param lista
	 * 		Lista de objeto da classe ObjetoVo
	 * @return
	 * 		Lista de contas formatadas para cl�sula IN da SQL. J� trata a limita��o de 1000 linhas por cl�usula IN. 
	 */
	public static List<String> listaContasClausulaIn(List<? extends ObjetoUtilVo> lista){
		Integer indexList = 0;
		Boolean comVirgula = false;
		StringBuilder sb = new StringBuilder(255);
		Integer count = 0;
		List<String> listaContas = new ArrayList<String>();
		while (indexList < lista.size()){
			count++;
			if (comVirgula){
				sb.append(", '" + lista.get(indexList).getConta() + "'");
			}else{
				sb.append("'" + lista.get(indexList).getConta() + "'");
				comVirgula = true;
			}
			if (count == 1000){
				comVirgula = false;
				listaContas.add(sb.toString());
				sb = new StringBuilder(255);
				count = 0;
			}else{
				if (indexList == lista.size()-1){
					listaContas.add(sb.toString());
				}
			}
			indexList++;
		}
		return listaContas;
	}
	
	
	
	
	/**
	 * Ese m�todo retorna uma lista de valorespara cl�sula IN da SQL, tratando a limita��o de 1.000 valores por cl�usula IN.
	 * Usar a lista de retorno para executar a mesma query mais de uma vez passando cada linha da lista na c�usula IN.
	 * 
	 * 
	 * @author 80480943 - Andr� Luiz S. Teot�nio
	 * @since 21/03/2017
	 * 
	 * @param nomeAtributo
	 * Nome do atributo que possui o valor que ir� ser retornado na lista. Esse atributo deve ter um m�todo get p�blico.
	 * Ex.: atributo conta, deve ter o m�todo p�bico getConta
	 * 
	 * @param listaObjetos
	 * Lista gen�rica de qualquer classe. Ex.: List<MeuObjeto>, List<MeuObjetoFilho>, etc.
	 * 
	 * @param forceStringSQL
	 * Esse par�metro for�a os valores retornados na lista a virem entre '' (aspas simples), ignorando o tipo de retorno 
	 * do m�todo get do atributo desejado
	 * Ex. com forceStringSQL = Util.VALORES_COMO_STRING_SQL: '12345', '1245'
	 * Ex. com forceStringSQL = Util.VALORES_NORMAIS_SQL: 12345, 1245
	 * Ex. com forceStringSQL = Util.VALORES_NORMAIS_SQL: 20.55, 100.01
	 * Ex. com forceStringSQL = Util.VALORES_COMO_STRING_SQL: '20.55', '100.01'
	 * Ex. com forceStringSQL = Util.VALORES_NORMAIS_SQL: 'JOS� SILVA', 'ANA MARIA'
	 * 
	 * 
	 * @return
	 * Lista de strings que retorna at� mil valores por linha separados por v�rgula.
	 * Ex.: '1245624154', '5465798321', etc.
	 * 
	 * 
	 * Exemplo de uso:
	 * List<String> litaCpf = Util.listarAtributoParaClausulaInSQL("cpf", listaObjetosCliente, Util.VALORES_COMO_STRING_SQL);
	 * List<String> litaNrc = Util.listarAtributoParaClausulaInSQL("typeIdNrc", listaObjetosNRC, Util.VALORES_NORMAIS_SQL);
	 * O retorn para listaNrc seria: 12848, 12848, 12850,...at� mil valores por linha
	 * 
	 * 
	 */
	public static List<String> listarAtributoParaClausulaInSQL(String nomeAtributo, List<?> listaObjetos, Boolean forceStringSQL) throws Exception {
		List<String> lista = new ArrayList<String>();
		Integer indexList = 0;
		Boolean comVirgula = false;
		StringBuilder sb = new StringBuilder(255);
		Integer count = 0;
		java.lang.reflect.Method method = null;
		java.lang.reflect.Type returnType = null;
		Class<?> classz;
		Object objeto;
		String nomeMetodoGet = null;
		String valor = null;
		Boolean returnAsString = false;
		
		if (listaObjetos == null)
			throw new Exception("Par�metro inv�lido: listaObjeto = null");
		if (listaObjetos.size() == 0)
			return lista;
		if (nomeAtributo == null)
			throw new Exception("Par�metro inv�lido: nomeAtributo = null");
		if (nomeAtributo.length() == 0)
			throw new Exception("Par�metro inv�lido: nomeAtributo vazio");

		try {
			
			objeto = listaObjetos.get(0);
			classz = objeto.getClass();
			nomeMetodoGet = "get"+ nomeAtributo.substring(0, 1).toUpperCase() + nomeAtributo.subSequence(1, nomeAtributo.length());
			for (java.lang.reflect.Method m : classz.getMethods()){
				if (m.getName().equals(nomeMetodoGet)){
					method = m;
					returnType = m.getReturnType();
				}
			}
			
			if (method == null)
				throw new Exception("Par�metro com valor incorreto: \"nomeAtributo\". M�todo get do atribudo n�o foi localizado na classe!");
				
			returnAsString = forceStringSQL?true:(returnType == String.class?true:false);
			
			while (indexList < listaObjetos.size()){
				objeto = listaObjetos.get(indexList);
				valor = String.valueOf(method.invoke(objeto));

				count++;
				if (comVirgula){
					sb.append(", " + (returnAsString?"'":"") + valor + (returnAsString?"'":""));
				}else{
					sb.append((returnAsString?"'":"") + valor + (returnAsString?"'":""));
					comVirgula = true;
				}
				if (count == 1000){
					comVirgula = false;
					lista.add(sb.toString());
					sb = new StringBuilder(255);
					count = 0;
				}else{
					if (indexList == listaObjetos.size()-1){
						lista.add(sb.toString());
					}
				}
				indexList++;
			}
			
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
			throw new Exception(e);
		}
		
		return lista;
	}

}
