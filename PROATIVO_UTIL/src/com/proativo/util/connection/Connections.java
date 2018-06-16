package com.proativo.util.connection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

import com.proativo.util.dao.OraUtilKenan;
import com.proativo.util.log.Log;

/**
 * Classe de controle de conexoes.
 * @author Rhuan Krum - G0030353
 *
 */
public class Connections {
	
	public static int QTDE_CUSTS_KENAN;

	public static final String CONN_PROATIVO = "PROATIVO_PGFIN";
	public static final String CONN_KENAN_CT = "PROATIVO_KENAN_CT";
	public static final String CONN_KENAN_CAT = "PROATIVO_KENAN_CAT";
	public static final String CONN_SIEBEL5 = "PROATIVO_SIEBEL5";
	public static final String CONN_SIEBEL8 = "PROATIVO_SIEBEL8";
	public static final String CONN_PCON = "PROATIVO_PCON";
	public static final String CONN_PTAB = "PROATIVO_PTAB";
	public static final String CONN_PNUM = "PROATIVO_PNUM";
	public static final String CONN_PSAV = "PROATIVO_PSAV";
	public static final String CONN_PSAVC8 = "PROATIVO_PSAVC8";
	public static final String CONN_PGEN = "PROATIVO_PGEN";
	public static final String CONN_DINTEGRACAO = "PROATIVO_DINTEGRACAO";
	public static final String CONN_SVCPDW1 = "SVCPDW1";
	public static final String CONN_CYBER = "CYBER";
	public static String[] CONNS_KENAN;
	
    private static List<Connection> conexoesAbertas = new ArrayList<Connection>();
    
    private static DBPoolManager poolMan;
    
    private static OraUtilKenan kenan = new OraUtilKenan();
    
    static {
    		try {
    			poolMan = DBPoolManager.getInstance();
    		} catch (DBPoolConfigFileFormatException e) {
    			Log.error("Existe um erro no formato de arquivos do DBPool: ", e);
    		} catch (ClassNotFoundException e) {
    			Log.error("Classe de Conexao nao encontrada: ", e);
    		}    	
    }
    
    /**
     * Retorna a conexao passada por parâmetro, buscando do Pool de Conexoes criado
     *
     * @param connName Nome da conexao configurada no arquivo jdbc.xml
     * @return Objeto com a conexao criada
     * @throws InterruptedException 
     * @throws SQLException Quando nao é possível obter conexao
     */
    public static synchronized Connection getConn(String connName) {
		
    	Connection conn = null;
    	int tries = 0;
    	int maxTries = 5;
    	int waitMinutes = 5;
    	
    	while(conn == null && ++tries <= maxTries){
	    	try {
				conn = poolMan.getConnection(connName);
				conexoesAbertas.add(conn);
				return conn;
			} catch (Exception ex) {	
				Log.error("Ocorreu um erro ao obter uma conexao do Pool de Conexoes para a conexao " + connName + ". Aguardando " + waitMinutes + " minutos para realizar uma nova tentativa. Tentativa " + tries + " de " + maxTries + ".", ex);

				// Aguarda "waitMinutes" minutos até realizar uma nova tentativa de conexao
				try {
					Thread.sleep(waitMinutes * 1000 * 60);
				} catch (InterruptedException e) {
					Log.error("Ocorreu um erro ao aguardar " + waitMinutes + " minutos para retentativa de conexao com a base " + connName + " apos " + tries + " tentativas.", e);
				}
			}
		}
    	
    	// Caso nao seja possível recuperar uma conexao a aplicaçao sera encerrada
    	if(conn == null){
    		Log.error("Ocorreu um erro ao obter uma conexao do Pool de Conexoes para a conexao "+connName + ". Todas as tentativas foram esgotadas! A aplicaçao sera encerrada!", new SQLException());
    		System.exit(-1);
    	}

		return conn;
    }
    
    
    
    /**
     * Método que retorna uma conexao para o CustomerID específico para a conta em questao.
     * Utilizado para conexoes feitas no Arbor/Kenan.
     * @param connName Nome da conexao a ser estabelecida. Utilizar prferencialmente constantes desta classe.
     * @param custDBId Identificador do Cust Arbor.
     * @return Retorna conexao estabelecida com um dos Custs do Arbor.
     * @throws SQLException É lançada quando nao for possível estabelecer a conexao.
     */
    public static Connection getConn(String connName, int custDBId) {
    	return getConn(connName+custDBId);
    }
    
    /**
     * Método que retorna uma lista de conexoes do Arbor. Na lista retornada contém as conexoes de Cust do Arbor.
     * @return Retorna List de conexoes.
     * @throws SQLException É lançada quando nao for possível estabelecer a conexao.
     */
    public static List<Connection> getConnectionsKenan() {
    	List<Connection> conns = new ArrayList<Connection>();
    	conns.add(Connections.getConn(Connections.CONN_KENAN_CT+1));
    	conns.add(Connections.getConn(Connections.CONN_KENAN_CT+2));
    	return conns;
    }
    
    public static String[] getNameConnectionsKenan(){
    	if(CONNS_KENAN == null){
	    	List<Integer> kenanDbIds = kenan.buscarKenanDbIds();
    		CONNS_KENAN = new String[kenanDbIds.size()];
	    	
	    	for(int i = 0; i< kenanDbIds.size();i++){
	    		CONNS_KENAN[i] = "KENAN_CT" + kenanDbIds.get(i);
	    	}
			QTDE_CUSTS_KENAN = kenanDbIds.size();
    	}
    	return CONNS_KENAN;
    }
    
    /**
     * Metodo para fechar todas as conexoes oferecidas durante a vida da aplicaçao.
     * A menos que todas as querys envolvidas sejam de consulta, este método nao
     * deve ser utilizado por reduzir o controle sobre as exceçoes.
     * Criado apenas para retro-compatibilidade com a classe IConexoes de BPO
     *
     * @author Andre Rosot
     */
    public synchronized static void closeConnections() {
    	for(Connection conn : conexoesAbertas){
    		close(conn);
    	}
    	conexoesAbertas.clear();
    }
    
    /**
	 * Efetua o fechamento de um objeto SQL. <br>
	 * Como na nova versao do Arbor - Kenan 12 - é utilizado uma mesma biblioteca de conexoes é impressendivel que o fechamento da conexao ou objetos SQL.<br>
	 * <p><b>Exemplo:</b></p>
	 * <p><code>Connection conn = Connections.get(Connections.CONN_ARBOR_CATALOGO);<br>
	 *    try { <br>
	 *  Statment st = conn.createStetment();<br>
	 *  ...<br>
	 *  fechar(st);<br>
	 *  }<br>
	 *  ... <br>
	 *  finaly { <br>
	 *  fechar(conn);<br>
	 *  }</code></p>
	 * @param w Objeto a ser fechado, podendo ser os seguintes objetos: DynamicConnection, Connection, PreparedStatement, Statement, CallableStatement ou ResultSet.
	 */
    public static void close(Wrapper w) {
    	try {
			if(w != null) {
				if(w instanceof Connection && !((Connection) w).isClosed())
					((Connection) w).close();
				else if(w instanceof PreparedStatement)
					((PreparedStatement) w).close();
				else if(w instanceof Statement)
					((Statement) w).close();
				else if(w instanceof ResultSet)
					((ResultSet) w).close();
				else if(w instanceof CallableStatement)
					((CallableStatement) w).close();
				else if(w instanceof DynamicConnection)
					((DynamicConnection) w).close();
			}
		} catch (Exception e) {
			Log.error("Falha ao fechar objeto sql "+w.toString(), e);
		}
    }
    
    /**
     * Metodo que efetua o fechamento de uma lista de objetos SQL.
     * @param ws Lista de objetos SQL.
     */
    public static void close(Wrapper... ws) {
    	for (Wrapper w : ws) {
			close(w);
		}
    }
    
    /**
     * Método que efetua o fechamento de conexoes em uma lista.
     * @param conns Lista de conexoes ativas.
     */
	public static void closeConnections(List<Connection> conns) {
    	for (Connection connection : conns) {
    		close(connection);
    	}
    }
    
    public static void reloadConfig() {    	
    	try{
    		DBPoolManager poolMan = DBPoolManager.getInstance();
    		poolMan.reloadConfig();
    	}
		catch (Exception ex){
			Log.error("Ocorreu um erro no Pool de Conexoes.", ex);
		}
    }
    
    /**
     * Realiza teste de se determinada conexão é válida
     *
     * @author G0030353 - Rhuan Krum
     * @since 12/06/2017
     */
    public static Boolean isConexaoValida(String connName) throws Exception{
		
    	Connection conn = null;
    	Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		
		Boolean conexaoValida = false;
		
    	try {
			conn = poolMan.getConnection(connName);
			
			sql = "select 1 from dual";
			stmt = conn.prepareStatement(sql);
			
			conexaoValida = (stmt.execute(sql));
		} catch (Exception e) {	
			conexaoValida = false;
			throw new Exception(e.getMessage());
		} finally{
			close(rs, stmt, conn);
		}
    	return conexaoValida;
    }
    
    /**
     * Realiza teste de se determinado dbpool é válido
     *
     * @author G0030353 - Rhuan Krum
     * @since 12/06/2017
     */
    public static Boolean isDbpoolCarregado(String connName) throws Exception{
    	try{
    		return poolMan.listarDbpoolsCarregados().contains(connName);
    	} catch(Exception e){
			throw new Exception(e.getMessage());
    	}
    }
}