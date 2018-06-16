package com.proativo.util.connection;

public class DBPoolConfigBean
{
	private String	poolName;

	private String	url1;
	private String	url2;
	private String	userName;
	private String	password;
	private String	validationQuery;
	private int		maxSize;

	public String getPoolName()
	{
		return poolName;
	}

	public void setPoolName(String poolName)
	{
		this.poolName = poolName;
	}

	public DBPoolConfigBean(
				String poolName,
				String url1,
				String url2,
				String userName,
				String password,
				String validationQuery,
				int maxSize)
	{
		this.poolName = poolName;
		this.url1 = url1;
		this.url2 = url2;
		this.userName = userName;
		this.password = password;
		this.validationQuery = validationQuery;
		this.maxSize = maxSize;
	}

	public String getUrl1()
	{
		return url1;
	}

	public String getUrl2()
	{
		return url2;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getPassword()
	{
		return password;
	}

	public String getValidationQuery()
	{
		return validationQuery;
	}

	public int getMaxSize()
	{
		return maxSize;
	}
}
