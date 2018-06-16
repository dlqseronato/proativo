package com.proativo.util.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import com.proativo.util.log.Log;

public class DBPoolConfigFileManager {
	
	private static final String CONFIG_DIR;

	private static final String PROPERTIES_KEY_POOL_JDBC_URL_1 = "pool.jdbc.url1";
	private static final String PROPERTIES_KEY_POOL_JDBC_URL_2 = "pool.jdbc.url2";
	private static final String PROPERTIES_KEY_POOL_JDBC_USER = "pool.jdbc.user";
	private static final String PROPERTIES_KEY_POOL_JDBC_PASSWORD = "pool.jdbc.password";
	private static final String PROPERTIES_KEY_POOL_VALIDITY_CHECK = "pool.validationQuery";
	private static final String PROPERTIES_KEY_POOL_MAX_SIZE = "pool.maxSize";
	
	static{
		String config = System.getenv("GVT_CONFIG_BD_DIR");		
		if(config == null){
			throw new RuntimeException("Variavel de ambiente GVT_CONFIG_BD_DIR nao encontrada.");
		}
		// deixa o caminho no formato D:/Billing/CONFIG_DIR/		
		config = config.replaceAll("\\\\", "/").trim();
		if(!config.endsWith("/")){
			config += "/";
		}
		CONFIG_DIR = config;
	}

	public DBPoolConfigFileManager() {
	}

	/**
	 * Retorna todos os arquivos com extensao .dbpool da pasta /config
	 * 
	 * @return
	 */
	private String[] getDBPoolPropertiesFiles() throws DBPoolConfigFileFormatException {

		String[] fileNames = null;

		try {
			File dir = new File(CONFIG_DIR);
			FilenameFilter filter = new DBPoolFileNameFilter();
			fileNames = dir.list(filter);
		} catch (Exception e) {
			throw new DBPoolConfigFileFormatException("Nao foi possivel ler os arquivos do diretorio: " + CONFIG_DIR);
		}

		return fileNames;
	}

	/**
	 * Carrega o arquivo como Properties.
	 * 
	 * @param propertiesFileName
	 * @return
	 */
	private Properties readProperties(String propertiesFileName) {
		Properties dbPoolProp = new Properties();

		try {
			dbPoolProp.load(new FileInputStream(propertiesFileName));
		} catch (FileNotFoundException e) {
			Log.error("Arquivo nao encontrado: " + propertiesFileName, e);
		} catch (IOException e) {
			Log.error("Nao foi possivel ler o arquivo: " + propertiesFileName, e);
		}

		return dbPoolProp;
	}

	/**
	 * Faz o mapeamento do arquivo .dbpool para o HashMap de argumentos.
	 * 
	 * @param dbpoolProperties
	 * @return
	 * @throws DBPoolConfigFileFormatException
	 */
	private Map<String, Object> loadJdbcPoolArgs(Properties dbpoolProperties) throws DBPoolConfigFileFormatException {
		Map<String, Object> args = new HashMap<String, Object>(0);

		// Pool 1
		String jdbcURL1 = this.getDBPoolConfigStringValue(dbpoolProperties, PROPERTIES_KEY_POOL_JDBC_URL_1, true);
		args.put(PROPERTIES_KEY_POOL_JDBC_URL_1, jdbcURL1);

		String jdbcURL2 = this.getDBPoolConfigStringValue(dbpoolProperties, PROPERTIES_KEY_POOL_JDBC_URL_2, false);
		args.put(PROPERTIES_KEY_POOL_JDBC_URL_2, jdbcURL2);

		String jdbcUser = this.getDBPoolConfigStringValue(dbpoolProperties, PROPERTIES_KEY_POOL_JDBC_USER, true);
		args.put(PROPERTIES_KEY_POOL_JDBC_USER, jdbcUser);

		String jdbcPassword = this
				.getDBPoolConfigStringValue(dbpoolProperties, PROPERTIES_KEY_POOL_JDBC_PASSWORD, true);
		args.put(PROPERTIES_KEY_POOL_JDBC_PASSWORD, jdbcPassword);

		String validationQuery = this.getDBPoolConfigStringValue(dbpoolProperties, PROPERTIES_KEY_POOL_VALIDITY_CHECK,
				true);
		args.put(PROPERTIES_KEY_POOL_VALIDITY_CHECK, validationQuery);

		Integer intMaxSize = this.getDBPoolConfigIntValue(dbpoolProperties, PROPERTIES_KEY_POOL_MAX_SIZE, true);
		args.put(PROPERTIES_KEY_POOL_MAX_SIZE, intMaxSize);

		return args;
	}

	/**
	 * Lê e verifica o formato da propriedade para retornar um valor String
	 * 
	 * @param dbpoolProperties
	 * @param propertyKey
	 * @return
	 * @throws DBPoolConfigFileFormatException
	 */
	private String getDBPoolConfigStringValue(Properties dbpoolProperties, String propertyKey, boolean isRequired) throws DBPoolConfigFileFormatException {
		String value = dbpoolProperties.getProperty(propertyKey);

		if (isRequired && value == null){
			throw new DBPoolConfigFileFormatException("Argumento " + propertyKey + " nao pode ser nulo!");
		}

		return value;
	}

	/**
	 * Lê e verifica o formato da propriedade para retornar um valor Integer
	 * 
	 * @param dbpoolProperties
	 * @param propertyKey
	 * @return
	 * @throws DBPoolConfigFileFormatException
	 */
	private Integer getDBPoolConfigIntValue(Properties dbpoolProperties, String propertyKey, boolean isRequired)
			throws DBPoolConfigFileFormatException {
		Integer intValue = new Integer(0);

		String value = dbpoolProperties.getProperty(propertyKey).trim();
		if (isRequired && value == null)
			throw new DBPoolConfigFileFormatException("Argumento " + propertyKey + " nao pode ser nulo!");

		try {
			intValue = new Integer(value);
		} catch (NumberFormatException e) {
			// Se o valor for requirido, entao gera a Excecao
			if (isRequired)
				throw new DBPoolConfigFileFormatException("Argumento " + propertyKey + " deve ser inteiro! (valor="
						+ value + ")");
		}

		return intValue;
	}

	/**
	 * Lê os arquivos de configuracao do DBPool e carrega um Iterator de objetos
	 * DBPoolConfigBean.
	 * 
	 * @return
	 */
	public Iterator<DBPoolConfigBean> loadPoolConfig() throws DBPoolConfigFileFormatException {
		Iterator<DBPoolConfigBean> retorno = null;
		Vector<DBPoolConfigBean> poolList = new Vector<DBPoolConfigBean>(0);

		String propertiesFileNames[] = this.getDBPoolPropertiesFiles();

		// Se existir algum arquivo de configuracao
		if (propertiesFileNames != null && propertiesFileNames.length > 0) {
			for (int i = 0; i < propertiesFileNames.length; i++) {
				String propertiesFileName = propertiesFileNames[i];

				String poolName = propertiesFileName.substring(0, propertiesFileName.lastIndexOf("."));

				Map<String, Object> args = loadJdbcPoolArgs(this.readProperties(CONFIG_DIR + propertiesFileName));

				poolList.add(new DBPoolConfigBean(poolName, (String) args.get(PROPERTIES_KEY_POOL_JDBC_URL_1),
						(String) args.get(PROPERTIES_KEY_POOL_JDBC_URL_2), (String) args
								.get(PROPERTIES_KEY_POOL_JDBC_USER), (String) args
								.get(PROPERTIES_KEY_POOL_JDBC_PASSWORD), (String) args
								.get(PROPERTIES_KEY_POOL_VALIDITY_CHECK), ((Integer) args
								.get(PROPERTIES_KEY_POOL_MAX_SIZE)).intValue()));
			}

			retorno = poolList.iterator();
		} else {
			throw new DBPoolConfigFileFormatException("Nenhum arquivo de configuracao encontrado!");
		}

		return retorno;
	}
	
	/**
	 * Método para capturar a quantidade de sessões disponiveis de uma conexao configurada em um arquivo dbpool
	 * 
	 * @author G0030353 - Rhuan Pablo Ribeiro Krum
	 * @since 04/07/2013
	 * 
	 * @param connectionName
	 * @return
	 * @throws DBPoolConfigFileFormatException
	 */
	public Integer getAvailableConnections(String connectionName) throws DBPoolConfigFileFormatException{
		return this.getDBPoolConfigIntValue(this.readProperties(CONFIG_DIR + connectionName + ".dbpool"), PROPERTIES_KEY_POOL_MAX_SIZE, true);
	}
		
}
