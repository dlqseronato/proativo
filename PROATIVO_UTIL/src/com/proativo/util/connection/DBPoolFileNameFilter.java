package com.proativo.util.connection;

import java.io.File;
import java.io.FilenameFilter;

public class DBPoolFileNameFilter implements FilenameFilter
{
	public boolean accept(File dir, String name)
	{
		return name.endsWith(".dbpool");
	}
}
