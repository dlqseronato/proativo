package com.proativo.util.connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.proativo.util.log.Log;


public class ApacheDBCPConnectionManager
{
	private DataSource					_ds							= null;
	private PoolableConnectionFactory	_poolableConnectionFactory	= null;

	public ApacheDBCPConnectionManager(
				String connectURI,
				String userName,
				String password,
				int minIdle,
				int maxSize,
				String validationQuery) {		
		this._ds = this.createDataSource(connectURI, userName, password, minIdle, maxSize, validationQuery);
	}

	public DataSource getDataSource() {
		return this._ds;
	}

	public void logDriverStats() throws Exception {
		ObjectPool connectionPool = _poolableConnectionFactory.getPool();
		Log.info("NumActive: " + connectionPool.getNumActive());
		Log.info("NumIdle: " + connectionPool.getNumIdle());
	}

	private DataSource createDataSource(
				String connectURI,
				String userName,
				String password,
				int minIdle,
				int maxSize,
				String validationQuery) {
		//
		// First, we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		GenericObjectPool connectionPool = new GenericObjectPool(null);
		connectionPool.setMinIdle(minIdle);
		connectionPool.setMaxActive(maxSize);
		connectionPool.setTestWhileIdle(true);
		connectionPool.setTestOnReturn(true);
		connectionPool.setTestOnBorrow(true);

		//
		// Next, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, userName, password);

		//
		// Now we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		_poolableConnectionFactory =
					new PoolableConnectionFactory(connectionFactory, connectionPool, null, validationQuery, false, true);
		
		//
		// Finally, we create the PoolingDataSource itself,
		// passing in the object pool we created.
		//
		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

		return dataSource;
	}
}