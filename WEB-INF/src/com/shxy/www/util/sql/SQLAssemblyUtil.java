package com.shxy.www.util.sql;

import java.util.Map;

import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.DataOperateFactory;

/**
 * 专用从各种数据结构中获得数据源，进行拼接SQL
 * @author 藕旺江
 *
 */
public class SQLAssemblyUtil {
	
	protected DBManager dbManager = null;
	
	public SQLAssemblyUtil(DBManager dbManager){
		this.dbManager = dbManager;
	}
	
	/**
	 * 将Map中信息转换为SQL语句
	 * @param strMap 数据源 
	 * @return
	 * 	拼接的字符串
	 * @throws Exception
	 */
	public String map2SQL(Map<String, String> strMap) throws Exception{
		return "";
	}
	
	/**
	 * 构造SQLAssemblyUtil对象
	 * @param dbManager 数据库连接对象
	 * @return
	 * 			1		： ORACLE拼接对象
	 * 			2		： sqlite拼接对象
	 */
	public static SQLAssemblyUtil factory(DBManager dbManager){
		int type = DataOperateFactory.getDBType(dbManager);
		SQLAssemblyUtil sqlUtil = null;
		switch(type){
		case 1: sqlUtil = new SQLAssemblyUtilOfOracle(dbManager); break;
		case 4: sqlUtil = new SQLAssemblyUtilOfSqlite(dbManager);break;
		default: sqlUtil = new SQLAssemblyUtil(dbManager);
		}
		return sqlUtil;
	}
}
