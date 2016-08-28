package com.bevelio.ultragames.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager
{
	private HikariConfig config;
	private HikariDataSource datasource;
	
	public DatabaseManager(String ip, String port, String database, String username, String password)
	{
		this.init(ip, port, database, username, password);
	}
	
	public DatabaseManager(DatabaseInfo databaseInfo) 
	{
		this(databaseInfo.ip, databaseInfo.port, databaseInfo.database, databaseInfo.username, databaseInfo.password);
	}

	public void init(String ip, String port, String database, String username, String password)
	{
		config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + ip +":" + port + "/" + database);
		config.setUsername(username);
		config.setPassword(password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		datasource = new HikariDataSource(config);
	}
	
	public HikariDataSource getDataSource()
	{
		return this.datasource;
	}
}
