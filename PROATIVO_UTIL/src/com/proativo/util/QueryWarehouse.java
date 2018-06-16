package com.proativo.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Classe que irá buscar no diretório SQL da aplicação e em seus subdiretórios (apenas até o primeiro
 * nível) os arquivos SQL, gravando na hashtable local. Essa classe será chamada pelos métodos DAO
 * para retornar o corpo das queries.
 * 
 * @author G0004429
 *
 */
public class QueryWarehouse{
	public static File diretorioUtil;
	
	/**
	 * Atributo que armazena como valor as queries e como chave o nome do arquivo contendo a query 
	 */
	private static Hashtable<String, String> warehouse;
	
	public static void init(){
		
		warehouse = new Hashtable<String, String>();
		
		File sqlDir = new File("sql");
		
		if(sqlDir.exists() && sqlDir.isDirectory()){
			getSQL(sqlDir);
		}
		
		if (diretorioUtil.exists() && diretorioUtil.isDirectory()) {
			getSQL(diretorioUtil);
		}
	}
	
	/**
	 * 
	 * @param root
	 */
	public static void getSQL(File root) {
		if (root.isFile()) {
			put(root);
			return;
		}
		File[] files = root.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().endsWith(".sql")) {
				put(files[i]);
			}
			if (files[i].isDirectory()) {
				getSQL(files[i]);
			}
		}
	}
	
	/** 
	 * Método que adiciona na hashtable 
	 * 
	 * @param file A referência para o arquivo contendo a query
	 */
	private static void put(File file)
	{
		String fileName = file.getName();
		
		warehouse.put(fileName.substring(0, fileName.indexOf(".sql")), TextFileUtil.getContents(file));
	}
	
	/**
	 * Método que busca o conteúdo da query
	 * 
	 * @param key O nome da query que é o nome do arquivo sql onde está a mesma
	 * @return O conteúdo da query
	 */
	public static String getQuery(String key){
		if(warehouse == null){
			init();
		}
		
		Object o = warehouse.get(key);
		
		return o == null ? "" : (String) o;
	}
	
	/**
	 * Método que busca todas as queries com a substring keyBase no nome.
	 * 
	 * @param keyBase Substring para consulta de queries
	 * @return List de Strings contendo os nomes das queries encontradas
	 */
	public static List<String> getQueriesKeys(String keyBase)
	{
		List<String> queries = new ArrayList<String>();
		Set<String> keys = warehouse.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String key = (String) it.next();
			if (key.indexOf(keyBase) != -1){
				queries.add(key);
			}			
		}		
		return queries;
	}
}