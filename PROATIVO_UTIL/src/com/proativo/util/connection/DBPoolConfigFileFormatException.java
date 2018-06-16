package com.proativo.util.connection;

public class DBPoolConfigFileFormatException extends Exception
{
	private static final long	serialVersionUID	= 1L;

	public DBPoolConfigFileFormatException()
	{
	}

	public DBPoolConfigFileFormatException(String message)
	{
		super(message);
	}

	public DBPoolConfigFileFormatException(Throwable cause)
	{
		super(cause);
	}

	public DBPoolConfigFileFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
