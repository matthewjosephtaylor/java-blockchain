package com.thaler.miner.localdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.h2.Driver;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.ddb.HashableByteArray;
import com.thaler.miner.merkle.Hashable;

public class LocalDatabase {

	private static final Logger logger = Logger.getLogger(LocalDatabase.class);

	private static final long H2_CACHE_SIZE_KB = 1024 * 10;

	private static final String H2_OPTIONS = ";CACHE_SIZE=" + H2_CACHE_SIZE_KB;

	public static final String LOCAL_DATABASE_FILEPATH = "durableMessageStore.filepath";
	
	
	private static final String CONNECTION_POOL_JDBC_DRIVER_URI = "jdbc:apache:commons:dbcp:";
	private static final String CONNECTION_POOL_NAME = "connection_pool";

	private static final String CONNECTION_POOL_URI = "jdbc:apache:commons:dbcp:" + CONNECTION_POOL_NAME;

	private static final int LARGEST_VALUE_SIZE_BYTES = 4096;

	
	private static LocalDatabase instance;
	
	private LocalDatabase(){
		setupDb();
	}
	
	public static LocalDatabase getInstance(){
		if(null == instance){
			instance = new LocalDatabase();
		}
		return instance;
	}
	
	public void put(Bits256 key, Hashable value){
		Preconditions.checkNotNull(key);
		Preconditions.checkNotNull(value);

		if(exists(key)){
			return;
		}
		
		Connection connection = getConnection();
		try {
			final PreparedStatement statement = getPreparedStatement(connection, "INSERT INTO KEY_VALUE_PAIRS(KEY,VALUE) VALUES(?,?)");
			statement.setBytes(1, key.getBytes());
			statement.setBytes(2, value.getBytes());
			final int result = statement.executeUpdate();
			if (result != 1) {
				throw new RuntimeException("Error inserting into local database.  key: " + key.toString());
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeConneciton(connection);
		}
	}
	
	public boolean exists(Bits256 key){
		return get(key).isPresent();
	}
	
	public Optional<Hashable> get(Bits256 key){
		Preconditions.checkNotNull(key);
		Connection connection = getConnection();
		try {
			final PreparedStatement statement = getPreparedStatement(connection, "SELECT VALUE FROM KEY_VALUE_PAIRS");
			final ResultSet resultSet = statement.executeQuery();

			Hashable result;
			
			if(resultSet.first()){
				result = HashableByteArray.create(resultSet.getBytes(1));
			}else{
				result = null; 
			}
			return Optional.fromNullable(result);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeConneciton(connection);
		}
	}
	
	private PreparedStatement getPreparedStatement(Connection connection, final String statement) {
		PreparedStatement result;
		try {
			result = connection.prepareStatement(statement);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private void setupDb() {
		try {
			Class.forName(Driver.class.getName());
			Class.forName(PoolingDriver.class.getName());
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		setupDbConnectionPool();
		setupDbSchema();
	}

	private void setupDbSchema() {
		executePreparedStatements(
				"CREATE CACHED TABLE IF NOT EXISTS KEY_VALUE_PAIRS(KV_ID IDENTITY, KEY BINARY(32), VALUE BINARY("+LARGEST_VALUE_SIZE_BYTES+"), CREATED LONGVARCHAR)",
				"CREATE UNIQUE INDEX IF NOT EXISTS KEY_INDEX ON KEY_VALUE_PAIRS(KEY)");
	}

	private void executePreparedStatements(final String... statements) {
		Connection connection = getConnection();
		try {

			for (String statement : statements) {
				{
					final CallableStatement callableStatement = connection.prepareCall(statement);
					callableStatement.execute();
				}
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeConneciton(connection);
		}
	}

	private static void closeConneciton(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	public Connection getConnection() {
		Connection connection;
		try {
			connection = DriverManager.getConnection(CONNECTION_POOL_URI);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return connection;
	}

	private void setupDbConnectionPool() {
		try {
			final String durableMessageStoreFile = System.getProperty(LOCAL_DATABASE_FILEPATH, "/tmp/miner");
			logger.info("DurableMessageSender storage directory: " + durableMessageStoreFile);

			String jdbcUri = "jdbc:h2:" + durableMessageStoreFile + H2_OPTIONS;

			ObjectPool connectionPool = new GenericObjectPool(null);
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUri, null);
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

			Class.forName("org.apache.commons.dbcp.PoolingDriver");
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(CONNECTION_POOL_JDBC_DRIVER_URI);

			driver.registerPool(CONNECTION_POOL_NAME, connectionPool);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	public static void logConnectionPoolDriverStats() {
		PoolingDriver driver;
		try {
			driver = (PoolingDriver) DriverManager.getDriver(CONNECTION_POOL_JDBC_DRIVER_URI);
			ObjectPool connectionPool = driver.getConnectionPool(CONNECTION_POOL_NAME);

			logger.info("Connection Pool NumActive: " + connectionPool.getNumActive());
			logger.info("Connection Pool NumIdle: " + connectionPool.getNumIdle());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
