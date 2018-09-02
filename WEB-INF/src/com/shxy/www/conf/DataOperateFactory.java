package com.shxy.www.conf;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * 数据库适配器对象工厂
 * @author anqian
 *
 */
public class DataOperateFactory {
	
	/**
	 * 获取数据库类型
	 * @param dbManager
	 * @return 返回数据库标识，1：oracle 2：SqlServer 3：MySQL -1：发生异常 0：其他值
	 */
	public static int getDBType(DBManager dbManager)
	{
		try{
			Connection con = dbManager.getConnection();
			DatabaseMetaData dbmd=con.getMetaData();//获取DatabaseMetaData对象
			String dbName = dbmd.getDatabaseProductName();//获取数据库产品名称
			if(dbName.equals("Oracle"))
			{
				return 1;
			}else if(dbName.equals("Microsoft SQL Server"))
			{
				return 2;
			}else if(dbName.equals("MySQL"))
			{
				return 3;
			}else if(dbName.equals("SQLite")){
				return 4;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
}

