package com.shxy.www.model;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.StringUtil;

public class IPManageModel {
	
	private DBManager dbManager = null;

	public IPManageModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}

	/**
	 * 获得用户连接状态信息
	 * @param author 用户ID
	 * @return
	 * 		Map[
	 * 			IP		: 数据库IP地址
	 * 			time	: 连接时间
	 * 			type	: 数据库类型
	 * 		]
	 * @throws Exception
	 */
	public Map<String, Object> connStatus(String author) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		/*****************20140514修改，添加S003、S004***********************/
		
		String sql = "SELECT S2.S001,S1.S003,S2.S002,S2.ID,S2.S003,S2.S004 FROM SYSTAB202 S1,SYSTAB201 S2 WHERE S1.S002=S2.ID AND S1.S001='param1'"
			.replaceAll("param1", author);
		List<List> queryResult = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(queryResult)){
			return result;
		}
		result.put("IP", queryResult.get(0).get(0));
		result.put("time", queryResult.get(0).get(1));
		result.put("type", queryResult.get(0).get(2));
		/*****************20140514修改***********************/
		result.put("dbUserName", queryResult.get(0).get(4));
		result.put("dbPassword", queryResult.get(0).get(5));
		return result;
	}
	
	/**
	 * 断开连接
	 * @param author 用户ID
	 * @param ipType 数据库类型
	 * @return
	 * 		0	: 参数错误
	 * 		1	: 断开成功
	 * @throws Exception
	 */
	public String disConn(String author, String ipType) throws Exception{
		String result = "0";
		if(StringUtil.arrIsNULL(author, ipType)){
			return result;
		}
		String sql = "DELETE FROM SYSTAB202 WHERE S001='param1' AND S002 IN(SELECT ID FROM SYSTAB201 WHERE S002='param2')"
			.replaceAll("param1", author)
			.replaceAll("param2", ipType);
		this.dbManager.executeUpdateSQL(sql);
		result = "1";
		return result;
	}
	
	/**
	 * 添加连接信息
	 * @param author 添加者
	 * @param ips ip地址
	 * @return
	 * 		0	: 参数错误
	 * 		1	: 添加成功
	 * @throws Exception
	 */
	public String conn(String author, String ips) throws Exception{
		String result = "0";
		if(StringUtil.arrIsNULL(author, ips)){
			return result;
		}
		String sql = "DELETE FROM SYSTAB202 WHERE S001=param1"
			.replaceAll("param1", StringUtil.strAddQuote(author));
		this.dbManager.executeUpdateSQL(sql);
		sql = "INSERT INTO SYSTAB202(S001,S002) VALUES('param1','param2')"
			.replaceAll("param1", author)
			.replaceAll("param2", ips);
		this.dbManager.executeInsertSQL(sql);
		result = "1";
		return result;
	}
	
	/**
	 * 添加数据库IP
	 * @param ip 数据库IP地址
	 * @param type 数据库类型（1=机关 0=基层）
	 * @return
	 * 		0	: 参数错误
	 * 		1	: 添加成功
	 * @throws Exception
	 */
	public String addIP_old(String ip, String type) throws Exception{
		String result = "0";
		if(StringUtil.arrIsNULL(ip, type)){//判断IP和数据库类型是否为空，是空，返回result
			return result;
		}
		//向systab201表中插入一个IP
		String sql = "INSERT INTO SYSTAB201(S001,S002) VALUES('param1','param2')"
			.replaceAll("param1", ip)
			.replaceAll("param2", type);
		this.dbManager.executeInsertSQL(sql);
		result = "1";
		return result;
	}
	/**
	 * 添加数据库IP(20140514修改)
	 * @param ip 数据库IP地址
	 * @param type 数据库类型（1=机关 0=基层）
	 * @return
	 * 		0	: 参数错误
	 * 		1	: 添加成功
	 * @throws Exception
	 */
	public String addIP(String ip, String type,String dbUserName,String dbPassword) throws Exception{
		String result = "0";
		if(StringUtil.arrIsNULL(ip, type)){//判断IP和数据库类型是否为空，是空，返回result
			return result;
		}
		dbPassword="".equals(dbPassword)?"shanghaixinyuan":dbPassword;
		//向systab201表中插入一个IP
		String sql = "INSERT INTO SYSTAB201(S001,S002,S003,S004) VALUES('param1','param2','param3','param4')"
			.replaceAll("param1", ip)
			.replaceAll("param2", type)
			.replaceAll("param3", dbUserName)
			.replaceAll("param4", dbPassword);/*****************20140514修改，添加S003、S004***********************/
		this.dbManager.executeInsertSQL(sql);
		result = "1";
		return result;
	}
	
   public String deleteIP(String id)throws Exception{
	   String result="0";
	   if(StringUtil.arrIsNULL(id)){
			return result;
		}
	   if(id.endsWith(",")){
		   id=id.substring(0,id.length()-1);
	   }
	   String sql = "delete from SYSTAB201 where id in('"+id+"')";
	   this.dbManager.executeDeleteSQL(sql);
	   result="1";
	   return result;
   }
	
}
